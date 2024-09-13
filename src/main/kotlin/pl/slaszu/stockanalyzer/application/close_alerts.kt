package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import pl.slaszu.shared_kernel.domain.roundTo
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.alert.CloseAlertService
import pl.slaszu.stockanalyzer.domain.chart.ChartBuilder
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import java.time.LocalDateTime

@Service
class CloseAlerts(
    private val stockProvider: StockProvider,
    private val alertRepo: AlertRepository,
    private val closeAlertRepo: CloseAlertRepository,
    private val chartProvider: ChartProvider,
    private val publisher: Publisher,
    private val closeAlertService: CloseAlertService,
    private val chartForAlert: ChartForAlert,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    fun runForDaysAfter(daysAfter: Int, andClose: Boolean = false) {

        val date = LocalDateTime.now().minusDays(daysAfter.toLong())

        val alerts = this.alertRepo.findAlertsActiveBeforeThatDate(date)

        this.logger.info { "Get alert before $daysAfter days [date : ${date.toString()}]" }
        this.logger.info { "Alerts found qty : ${alerts.size}" }

        val findByDaysAfter = this.closeAlertRepo.findByDaysAfterAndAlertClose(daysAfter)

        alerts.filter {
            val find = findByDaysAfter.find { closeAlertModel -> closeAlertModel.alert.stockCode == it.stockCode }
            find == null
        }.also {
            this.logger.info { "Alerts to check qty : ${it.size}" }
        }.forEach { alert ->
            // get stock price now
            val stockPriceList = this.stockProvider.getStockPriceList(alert.stockCode)
            val first = stockPriceList.first()

            this.logger.info {
                "Stock ${alert.stockCode} had price ${alert.price} " +
                        "and now has price ${first.price} [${first.updatedAt.toString()}]"
            }

            if (first.volume == 0) {
                this.logger.debug { "Skip this stock" }
                return@forEach // continue
            }

            var closeAlert = CloseAlertModel(
                alert = alert,
                resultPercent = this.getPriceChangePercent(alert.price, first.price),
                daysAfter = daysAfter,
                price = first.price
            )

            if (alert.shouldBePublish()) {
                closeAlert = closeAlert.copy(
                    tweetId = this.publishCloseAndGetId(closeAlert, stockPriceList)
                )
            }

            // add CloseAlertModel
            this.closeAlertService.persistCloseAlert(closeAlert)

            if (andClose) {
                this.closeAlertService.closeAlert(alert)
            }
        }
    }

    private fun getPriceChangePercent(buy: Float, sell: Float): Float {
        return (((100 * sell) / buy) - 100).roundTo(2)
    }

    private fun publishCloseAndGetId(closeAlert: CloseAlertModel, priceList: Array<StockPriceDto>): String {

        val alert = closeAlert.alert
        val alertLabel = closeAlert.getTitle()

        val buyPoint = this.chartForAlert.getBuyPoint(alert, priceList)
        val closePoint = this.chartForAlert.getSellPoint(closeAlert, priceList)

        // get chart png
//        val pngByteArray = this.chartProvider.getPngByteArray(
//            alert.stockCode,
//            priceList,
//            buyPoint, // buy point
//            ChartPoint(priceList.first(), closePrice, alertLabel) // close point
//        )
        val pngByteArray = ChartBuilder.create(this.chartProvider) {
            this.closeAlert = closeAlert
            this.buyPoint = buyPoint
            this.closePoint = closePoint
            this.stockPriceList = priceList
        }.getPng()

        // tweet alert
        return this.publisher.publish(
            pngByteArray,
            "$alertLabel (after ${closeAlert.daysAfter} days) | result: ${closeAlert.resultPercent} %",
            "#${alert.stockCode} #${alert.stockName} " +
                    "#gpwApiSignals\nhttps://pl.tradingview.com/symbols/GPW-${alert.stockCode}/",
            alert.tweetId
        )
    }
}
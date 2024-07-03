package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import pl.slaszu.shared_kernel.domain.roundTo
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.alert.CloseAlertService
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
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

            val priceChangeInPercent = this.getPriceChangePercent(alert.price, first.price)

            var publishedId:String? = null
            if (alert.shouldBePublish()) {
                publishedId = this.publishCloseAndGetId(alert, stockPriceList, daysAfter)
            }

            // add CloseAlertModel
            this.closeAlertService.persistCloseAlert(
                CloseAlertModel(
                    alert,
                    publishedId,
                    priceChangeInPercent,
                    daysAfter,
                    first.price
                )
            )

            if (andClose) {
                this.closeAlertService.closeAlert(alert)
            }
        }
    }

    private fun getPriceChangePercent(buy: Float, sell: Float): Float {
        return (((100 * sell) / buy) - 100).roundTo(2)
    }

    private fun publishCloseAndGetId(alert: AlertModel, priceList: Array<StockPriceDto>, daysAfter: Int): String {
        val first = priceList.first()

        val closePrice = first.price

        val priceChangeInPercent = this.getPriceChangePercent(alert.price, first.price)

        val alertLabel = "SELL ${alert.stockCode} $closePrice PLN"

        // find priceListElement for alert by date
        val priceListForAlert = priceList.find {
            it.date == alert.date.toLocalDate()
        }
        var buyPoint: ChartPoint? = null;
        if (priceListForAlert != null) {
            buyPoint = ChartPoint(priceListForAlert, alert.price, "BUY ${alert.stockCode} ${alert.price} PLN")
        }

        // get chart png
        val pngByteArray = this.chartProvider.getPngByteArray(
            alert.stockCode,
            priceList,
            buyPoint, // buy point
            ChartPoint(priceList.first(), closePrice, alertLabel) // close point
        )

        // tweet alert
        return this.publisher.publish(
            pngByteArray,
            "$alertLabel (after $daysAfter days) | result: $priceChangeInPercent %",
            "#${alert.stockCode} #${alert.stockName} " +
                    "#gpwApiSignals\nhttps://pl.tradingview.com/symbols/GPW-${alert.stockCode}/",
            alert.tweetId
        )
    }
}
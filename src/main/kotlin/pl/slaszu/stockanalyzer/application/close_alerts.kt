package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.model.AlertModel
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.model.CloseAlertRepository
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.shared.roundTo
import java.time.LocalDateTime

@Service
class CloseAlerts(
    private val stockProvider: StockProvider,
    private val alertRepo: AlertRepository,
    private val closeAlertRepo: CloseAlertRepository,
    private val chartProvider: ChartProvider,
    private val publisher: Publisher,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    fun runForDaysAfter(daysAfter: Int) {

        val date = LocalDateTime.now().minusDays(daysAfter.toLong())

        this.logger.info { "Get alert before $daysAfter days [date : ${date.toString()}]" }

        val alerts = this.alertRepo.findByDateBeforeAndCloseIsFalse(date)

        this.logger.info { "Alerts found qty : ${alerts.size}" }

        // for each alert do
        alerts.forEach { alert ->
            // get stock price now
            val stockPriceList = this.stockProvider.getStockPriceList(alert.stockCode)
            val first = stockPriceList.first()
            this.logger.info {
                "Stock ${alert.stockCode} had price ${alert.price} " +
                        "and now has price ${first.price} [${first.updatedAt.toString()}]"
            }

            val priceChangeInPercent = this.getPriceChangePercent(alert.price, first.price)


            //val tweetId = this.publishCloseAndGetId(alert, stockPriceList, daysAfter)
            val tweetId = "test"

            // add CloseAlertModel
            this.closeAlertRepo.save(
                CloseAlertModel(
                    alert,
                    tweetId,
                    priceChangeInPercent,
                    daysAfter
                )
            )
            return;
        }
    }

    private fun getPriceChangePercent(buy: Float, sell: Float): Float {
        return (((100 * sell) / buy) - 100).roundTo(2)
    }

    private fun publishCloseAndGetId(alert: AlertModel, priceList: Array<StockPriceDto>, daysAfter: Int): String {
        val first = priceList.first()

        val priceChangeInPercent = this.getPriceChangePercent(alert.price, first.price)

        val alertLabel = "SELL ${alert.stockCode} ${first.price} PLN"

        // find priceListElement for alert by date
        val priceListForAlert = priceList.find {
            it.date == alert.date.toLocalDate()
        }
        var buyPoint: ChartPoint? = null;
        if (priceListForAlert != null) {
            buyPoint = ChartPoint(priceListForAlert, "BUY ${alert.stockCode} ${alert.price} PLN")
        }

        // get chart png
        val pngByteArray = this.chartProvider.getPngByteArray(
            "${alert.stockCode}",
            priceList,
            buyPoint, // buy point
            ChartPoint(priceList.first(), alertLabel) // close point
        )

        // tweet alert
        return this.publisher.publish(
            pngByteArray,
            "$alertLabel | result: $priceChangeInPercent %",
            "#${alert.stockCode} #${alert.stockName} " +
                    "#gpwApiSignals\nhttps://pl.tradingview.com/symbols/GPW-${alert.stockCode}/",
            alert.tweetId
        )
    }
}
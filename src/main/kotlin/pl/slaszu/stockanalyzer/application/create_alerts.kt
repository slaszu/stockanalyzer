package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.roundTo
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.alert.AlertService
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalsChecker
import java.time.LocalDateTime

@Service
class CreateAlerts(
    private val stockProvider: StockProvider,
    private val signalProvider: SignalProvider,
    private val alertRepo: AlertRepository,
    private val alertService: AlertService,
    private val chartProvider: ChartProvider,
    private val publisher: Publisher,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    fun run() {
        val stockCodeList = this.stockProvider.getStockCodeList().also {
            logger.debug { "StockCodeList has ${it.size} qty" }
        }
        val date = LocalDateTime.now()
        val activeAlerts = this.alertRepo.findAlertsActiveBeforeThatDate(date)


        stockCodeList.filter {
            it.code != null // remove if code is null
        }.filter {
            val find = activeAlerts.find { alert ->
                alert.stockCode == it.code
            }
            find == null// remove if active alert for code exists
        }.also {
            logger.debug { "StockCodeList has ${it.size} qty after filter" }
        }.forEach {
            val stockPriceList = this.stockProvider.getStockPriceList(it.code!!)
            val signals = this.signalProvider.getSignals(stockPriceList)

            val signalsChecker = SignalsChecker(signals)

            if (signalsChecker.hasAll()) {

                logger.info {
                    "Code ${it.code} has all signals \n ${signals.contentToString()}}"
                }

                val alertModel = this.alertService.createAlert(
                    it,
                    stockPriceList.first().price,
                    signals.map { signal ->
                        signal.type.toString()
                    }
                )

                val publishedId = this.publishAlertAndGetId(alertModel, stockPriceList)

                alertService.persistAlert(alertModel.copy( tweetId = publishedId ))
                logger.info { "Saved alert: $alertModel" }
            }
        }
    }

    private fun publishAlertAndGetId(alert: AlertModel, priceList: Array<StockPriceDto>): String {

        val buyPrice = alert.price.roundTo(2)

        val alertLabel = "BUY ${alert.stockCode} $buyPrice PLN"

        // get chart png
        val pngByteArray = this.chartProvider.getPngByteArray(
            alert.stockCode,
            priceList,
            ChartPoint(priceList.first(), buyPrice, alertLabel)
        )

        // tweet alert
        return this.publisher.publish(
            pngByteArray,
            alertLabel,
            "#${alert.stockCode} #${alert.stockName} #gpwApiSignals\nhttps://pl.tradingview.com/symbols/GPW-${alert.stockCode}/"
        )
    }
}
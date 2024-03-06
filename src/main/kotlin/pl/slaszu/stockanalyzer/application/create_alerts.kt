package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.model.AlertModel
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.stock.StockDto
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalsChecker
import pl.slaszu.stockanalyzer.shared.roundTo
import java.time.LocalDateTime

@Service
class CreateAlerts(
    private val stockProvider: StockProvider,
    private val signalProvider: SignalProvider,
    private val alertRepo: AlertRepository,
    private val chartProvider: ChartProvider,
    private val publisher: Publisher,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    fun run() {
        val stockCodeList = this.stockProvider.getStockCodeList().also {
            logger.debug { "StockCodeList has ${it.size} qty" }
        }
        val date = LocalDateTime.now()
        val activeAlerts = this.alertRepo.findByDateBeforeAndCloseIsFalse(date)


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

                // todo uncomment
                val publishedId = this.publishAlertAndGetId(it, stockPriceList)
                //val publishedId = "test"


                val alertModel = AlertModel(
                    it.code,
                    it.name,
                    stockPriceList.first().price,
                    signals.map {
                        it.type
                    },
                    publishedId
                )

                alertRepo.save(alertModel)
                logger.info { "Saved alert: $alertModel" }

            }
        }
    }

    private fun publishAlertAndGetId(stock: StockDto, priceList: Array<StockPriceDto>): String {

        val alertLabel = "BUY ${stock.code} ${priceList.first().price.roundTo(2)} PLN"

        // get chart png
        val pngByteArray = this.chartProvider.getPngByteArray(
            "${stock.code}",
            priceList,
            ChartPoint(priceList.first(), alertLabel)
        )


        // tweet alert
        return this.publisher.publish(
            pngByteArray,
            alertLabel,
            "#${stock.code} #${stock.name} #gpwApiSignals\nhttps://pl.tradingview.com/symbols/GPW-${stock.code}/"
        )
    }
}
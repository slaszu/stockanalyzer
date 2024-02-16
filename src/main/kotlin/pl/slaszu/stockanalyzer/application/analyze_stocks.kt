package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.model.AlertModel
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalsChecker
import pl.slaszu.stockanalyzer.shared.toDate
import java.time.LocalDate.now

@Service
class GetStocksFromApiAnalyzeSignalLogicAndCreateAlerts(
    private val stockProvider: StockProvider,
    private val signalProvider: SignalProvider,
    private val alertRepo: AlertRepository,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    fun run() {
        val stockCodeList = this.stockProvider.getStockCodeList().also {
            logger.debug { "StockCodeList has ${it.size} qty" }
        }
        val date = now().toDate()
        val activeAlerts = this.alertRepo.findByDateAfterAndCloseIsFalse(date)


        stockCodeList.filter {
            it.code != null // remove if code is null
        }.filter {
            val find = activeAlerts.find { alert ->
                alert.stockCode == it.code
            }
            find == null// remove if active alert for code exists
        }.also {
            logger.debug { "StockCodeLost has ${it.size} qty after filter" }
        }.forEach {
            val stockPriceList = this.stockProvider.getStockPriceList(it.code!!)
            val signals = this.signalProvider.getSignals(stockPriceList)

            val signalsChecker = SignalsChecker(signals)

            if (signalsChecker.hasAll()) {

                logger.info {
                    "Code ${it.code} has all signals \n ${signals.contentToString() }}"
                }

                val alertModel = AlertModel(
                    it.code,
                    stockPriceList.first().price,
                    signals.map {
                        it.type
                    }
                )
                alertRepo.save(alertModel)
                logger.info { "Saved alert: $alertModel" }
            }
        }
    }
}
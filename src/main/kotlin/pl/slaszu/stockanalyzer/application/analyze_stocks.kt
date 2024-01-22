package pl.slaszu.stockanalyzer.application

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.domain.stockanalyzer.Signal
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalProvider

@Service
class GetStocksFromApiAnalyzeSignalLogicAndCreateAlerts(
    private val stockProvider: StockProvider,
    val signalProvider: SignalProvider
) {
    fun run() {
        val stockCodeList = this.stockProvider.getStockCodeList()
        stockCodeList.filter {
            it.code != null
        }.forEach {
            val stockPriceList = this.stockProvider.getStockPriceList(it.code!!)
            val signals = this.signalProvider.getSignals(stockPriceList)
            if (signals.isNotEmpty()) {
                println(it)
                signals.forEach { signal: Signal ->
                    println(signal)
                }
            }
        }
    }
}
package pl.slaszu.stockanalyzer.domain.stockanalyzer

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto


@Service
class SignalProvider(private val signalLogicList: List<SignalLogic>) {
    fun getSignals(priceList: Array<StockPriceDto>): Array<Signal> {
        val signalResultList = mutableListOf<Signal>()

        this.signalLogicList.forEach {
            val signal = it.getSignal(priceList)
            if (signal != null) {
                signalResultList.add(signal)
            }
        }

        return signalResultList.toTypedArray()
    }
}

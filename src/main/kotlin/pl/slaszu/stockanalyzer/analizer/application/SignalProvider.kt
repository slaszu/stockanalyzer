package pl.slaszu.stockanalyzer.analizer.application

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto

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

data class Signal(val name: String, val desc: String) {

}
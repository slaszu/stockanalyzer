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

enum class SignalEnum {
    PRICE_CHANGE_MORE_THEN_AVG_PERCENT,
    PRICE_CHANGE_MORE_THEN_HIGHEST_PERCENT,
    PRICE_HIGHEST
}

data class Signal(val type:SignalEnum, val desc: String, val data: Map<String, Float> = emptyMap()) {

}
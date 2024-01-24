package pl.slaszu.stockanalyzer.domain.stockanalyzer

enum class SignalEnum {
    HIGHEST_PRICE_FLUCTUATIONS_SINCE_FEW_DAYS,
    HIGHEST_PRICE_SINCE_FEW_DAYS
}

data class Signal(
    val type: SignalEnum,
    val desc: String,
    val data: Map<String, Float> = emptyMap()
) {
}

class SignalsChecker(private val signals: Array<Signal>) {
    private val signalsFromEntry = mutableListOf<SignalEnum>()

    init {
        this.signals.forEach {
            signalsFromEntry.add(it.type)
        }
    }

    fun hasAll(): Boolean {
        return signalsFromEntry.containsAll(SignalEnum.entries)
    }
}
package pl.slaszu.stockanalyzer.domain.stockanalyzer

enum class SignalEnum {
    PRICE_CHANGE_MORE_THEN_AVG_PERCENT,
    PRICE_CHANGE_MORE_THEN_HIGHEST_PERCENT,
    PRICE_HIGHEST
}

data class Signal(
    val type: SignalEnum,
    val desc: String,
    val data: Map<String, Float> = emptyMap()
) {
}
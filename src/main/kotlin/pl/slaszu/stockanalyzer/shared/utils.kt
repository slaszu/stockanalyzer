package pl.slaszu.stockanalyzer.shared

import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

fun Float.roundTo(numFractionDigits: Int): Float {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return ((this * factor).roundToInt() / factor).toFloat()
}

fun calcPercent(price: Float, price2: Float): Float {
    if (price.equals(0f) || price2.equals(0f)) {
        return 0f
    }
    return abs((price - price2) / price * 100)
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
}

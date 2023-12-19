package pl.slaszu.stockanalyzer.shared

import kotlin.math.pow
import kotlin.math.roundToInt

fun getResourceAsText(path: String): String? =
    object {}.javaClass.getResource(path)?.readText()

fun Float.roundTo(numFractionDigits: Int): Float {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return ((this * factor).roundToInt() / factor).toFloat()
}
package pl.slaszu.stockanalyzer.analizer.application.signal

import pl.slaszu.stockanalyzer.analizer.application.Signal
import pl.slaszu.stockanalyzer.analizer.application.SignalLogic
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import pl.slaszu.stockanalyzer.shared.roundTo
import kotlin.math.abs

// TODO: very often today change is 100, check this case
class PriceChangeMoreThenAvg(private val moreThenPercent: Int, private val avgFromLastDays: Int) : SignalLogic {
    override fun getSignal(priceList: Array<StockPriceDto>): Signal? {
        // sort by date desc
        priceList.sortByDescending { it.date }

        var end = avgFromLastDays
        if (avgFromLastDays > priceList.lastIndex) {
            end = priceList.lastIndex
        }
        // get first elements
        val sliceArray = priceList.sliceArray(0..<end)

        // sort slice by date asc
        sliceArray.reverse()

        // calculate avg change percent per days
        var sumPercent: Float = 0f
        var qty = 0;
        var vNext: StockPriceDto? = null
        for ((i, vPrev) in sliceArray.withIndex()) {
            val iNext = i + 1
            if (iNext >= sliceArray.lastIndex)
                break

            vNext = sliceArray[iNext]

            if (vNext.price.equals(0f) || vPrev.price.equals(0f)) {
                continue
            }

            // calc percent
            sumPercent += percent(vPrev, vNext)
            qty++
        }

        if (vNext == null || qty == 0) {
            return null
        }

        val avgPercent = (sumPercent / qty).roundTo(2)

        // calc latest price
        val percent = percent(vNext, sliceArray.last()).roundTo(2)

        // check condition
        if (percent >= avgPercent + moreThenPercent) {
            return createSignal(avgPercent, percent)
        }
        return null;
    }

    private fun percent(v: StockPriceDto, v2: StockPriceDto): Float {
        if (v.price.equals(0f)) {
            return 0f
        }
        return abs((v.price - v2.price) / v.price * 100)
    }

    private fun createSignal(avgPercent: Float, calculatedPercent: Float): Signal {
        return Signal(
            "Signal price change",
            "Avg change from last $avgFromLastDays days is $avgPercent. Change from today is $calculatedPercent !",
            mapOf("avgPercent" to avgPercent, "calculatedPercent" to calculatedPercent)
        )
    }
}
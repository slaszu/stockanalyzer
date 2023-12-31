package pl.slaszu.stockanalyzer.analizer.application.signal

import pl.slaszu.stockanalyzer.analizer.application.Signal
import pl.slaszu.stockanalyzer.analizer.application.SignalEnum
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import pl.slaszu.stockanalyzer.shared.calcPercent
import pl.slaszu.stockanalyzer.shared.roundTo

class PriceChangeMoreThenAvg(private val days: Int, private val moreThenPercent: Int) : SignalLogic {
    override fun getSignal(priceList: Array<StockPriceDto>): Signal? {

        /*
        1. sort and get last x days
        2. remove last day and remember it
        3. get max price from x days without last day
        4. if last day price i higher than y percent then signal
         */

        /*
        1. sort and get last x days
        2. remove last day and remember it
        3. get max price from x days without last day
        4. if last day price i higher than y percent then signal
         */

        if (priceList.size < 2 || days < 2) {
            // min 2 elements are required to work
            return null;
        }

        // 1
        priceList.sortByDescending { it.date }

        var end = days
        if (days > priceList.lastIndex) {
            end = priceList.lastIndex
        }

        // 2
        val sliceArray = priceList.sliceArray(1..end) // from 1, first is the latest day
        val latest = priceList.first()

        // sort slice by date asc
        sliceArray.reverse()

        // calculate avg change percent per days
        var sumPercent: Float = 0f
        var qty = 0;
        var vNext: StockPriceDto? = null
        for ((i, vPrev) in sliceArray.withIndex()) {
            val iNext = i + 1
            if (iNext > sliceArray.lastIndex)
                break

            vNext = sliceArray[iNext]

            if (vNext.price.equals(0f) || vPrev.price.equals(0f)) {
                continue
            }

            // calc percent
            sumPercent += calcPercent(vPrev.price, vNext.price).roundTo(2)
            qty++
        }

        if (vNext == null || qty == 0) {
            return null
        }

        val avgPercent = (sumPercent / qty).roundTo(2)

        // calc latest price
        val percent = calcPercent(vNext.price, latest.price).roundTo(2)

        // check condition
        if (percent >= avgPercent + moreThenPercent) {
            return createSignal(avgPercent, percent)
        }
        return null;
    }

    private fun createSignal(avgPercent: Float, calculatedPercent: Float): Signal {
        return Signal(
            SignalEnum.PRICE_CHANGE_MORE_THEN_AVG_PERCENT,
            "Price change $calculatedPercent%",
            mapOf("avgPercent" to avgPercent, "calculatedPercent" to calculatedPercent)
        )
    }
}
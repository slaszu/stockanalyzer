package pl.slaszu.stockanalyzer.analizer.application.signal

import pl.slaszu.stockanalyzer.analizer.application.Signal
import pl.slaszu.stockanalyzer.analizer.application.SignalEnum
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import pl.slaszu.stockanalyzer.shared.calcPercent
import pl.slaszu.stockanalyzer.shared.roundTo

class PriceAthSinceFewDays(private val days: Int, private val higherThenPercent: Int) : SignalLogic {
    override fun getSignal(priceList: Array<StockPriceDto>): Signal? {

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

        // 3
        val maxPriceHigh = sliceArray.maxOf { it.priceHigh }.roundTo(2)

        val percent = calcPercent(maxPriceHigh, latest.price).roundTo(2)

        if (percent > higherThenPercent && maxPriceHigh < latest.price) {
            return createSignal(
                latest.priceHigh, days, mapOf(
                    "maxPrice" to maxPriceHigh,
                    "lastPrice" to latest.price,
                    "higherThenPercent" to percent
                )
            )
        }
        return null
    }

    private fun createSignal(maxPrice: Float, days: Int, data: Map<String, Float>): Signal {
        return Signal(
            SignalEnum.PRICE_HIGHEST,
            "Price $maxPrice is the highest since $days days!",
            data
        )
    }
}
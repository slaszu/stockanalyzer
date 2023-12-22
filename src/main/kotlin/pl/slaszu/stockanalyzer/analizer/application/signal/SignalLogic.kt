package pl.slaszu.stockanalyzer.analizer.application.signal

import pl.slaszu.stockanalyzer.analizer.application.Signal
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto

interface SignalLogic {
    fun getSignal(priceList:Array<StockPriceDto>): Signal?
}
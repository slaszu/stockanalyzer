package pl.slaszu.stockanalyzer.analizer.application

import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto

interface SignalLogic {
    fun getSignal(priceList:Array<StockPriceDto>): Signal?
}
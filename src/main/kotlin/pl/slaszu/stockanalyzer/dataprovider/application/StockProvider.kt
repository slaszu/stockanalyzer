package pl.slaszu.stockanalyzer.dataprovider.application

interface StockProvider {
    fun getStockCodeList(): Array<StockDto>

    fun getStockPriceList(stockCode:String): Array<StockPriceDto>
}
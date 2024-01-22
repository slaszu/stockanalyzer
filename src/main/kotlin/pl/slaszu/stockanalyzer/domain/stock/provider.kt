package pl.slaszu.stockanalyzer.domain.stock

interface StockProvider {
    fun getStockCodeList(): Array<StockDto>

    fun getStockPriceList(stockCode: String): Array<StockPriceDto>
}
package pl.slaszu.stockanalyzer.domain.stock

import kotlinx.datetime.LocalDate

interface StockProvider {
    fun getStockCodeList(): Array<StockDto>

    fun getStockPriceList(stockCode: String): Array<StockPriceDto>

    fun getLastStockPriceList(stockCode: String, dateTo: LocalDate): Array<StockPriceDto>
}
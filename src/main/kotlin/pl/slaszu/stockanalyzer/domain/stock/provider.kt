package pl.slaszu.stockanalyzer.domain.stock

import kotlinx.datetime.LocalDate
import pl.slaszu.shared_kernel.domain.stock.StockDto
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto

interface StockProvider {
    fun getStockCodeList(): Array<StockDto>

    fun getStockPriceList(stockCode: String): Array<StockPriceDto>
}
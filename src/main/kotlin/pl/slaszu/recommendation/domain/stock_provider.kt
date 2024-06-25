package pl.slaszu.recommendation.domain

import kotlinx.datetime.LocalDate
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto

interface StockProvider {
    fun getLastStockPriceList(stockCode: String, dateTo: LocalDate): Array<StockPriceDto>
}
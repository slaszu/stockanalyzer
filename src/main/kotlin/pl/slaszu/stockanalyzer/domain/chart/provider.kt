package pl.slaszu.stockanalyzer.domain.chart

import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto

interface ChartProvider {
    fun getPngByteArray(code: String, priceList: Array<StockPriceDto>): ByteArray
}
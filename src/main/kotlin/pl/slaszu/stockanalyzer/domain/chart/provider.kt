package pl.slaszu.stockanalyzer.domain.chart

import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import java.util.Date

interface ChartProvider {

    fun getPngByteArray(
        code: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint? = null,
        closePoint: ChartPoint? = null
    ): ByteArray
}

data class ChartPoint(val point: StockPriceDto, val label: String)
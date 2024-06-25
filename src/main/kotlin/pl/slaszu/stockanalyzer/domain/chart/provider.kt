package pl.slaszu.stockanalyzer.domain.chart

import pl.slaszu.shared_kernel.domain.stock.StockPriceDto

interface ChartProvider {

    fun getPngByteArray(
        code: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint? = null,
        closePoint: ChartPoint? = null
    ): ByteArray
}

data class ChartPoint(val point: StockPriceDto, val pointValue: Float, val label: String)
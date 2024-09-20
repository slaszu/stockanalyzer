package pl.slaszu.stockanalyzer.domain.chart

import pl.slaszu.shared_kernel.domain.stock.StockPriceDto

interface ChartProvider {

    fun getPngByteArray(
        chartTitle: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint? = null,
        closePoint: ChartPoint? = null
    ): ByteArray

    fun getPngByteArray(
        chartTitle: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint? = null,
        closePointList: List<ChartPoint?> = emptyList()
    ): ByteArray
}

data class ChartPoint(val point: StockPriceDto, val pointValue: Float, val label: String)
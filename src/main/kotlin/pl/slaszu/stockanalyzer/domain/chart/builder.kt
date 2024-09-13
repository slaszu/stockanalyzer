package pl.slaszu.stockanalyzer.domain.chart

import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto

class ChartBuilder private constructor(
    private val chartProvider: ChartProvider
) {

    lateinit var title: String
    lateinit var alert: AlertModel
    lateinit var closeAlert: CloseAlertModel
    lateinit var stockPriceList: Array<StockPriceDto>
    var buyPoint: ChartPoint? = null
    var closePoint: ChartPoint? = null

    private fun getChartTitle(): String {
        if (::title.isInitialized) return title

        if (::closeAlert.isInitialized)
            return closeAlert.alert.stockName + " ${closeAlert.resultPercent} % (after ${closeAlert.daysAfter} days)"

        if (::alert.isInitialized) return alert.stockName

        return "Chart"
    }

    fun getPng(): ByteArray {

        check(!::stockPriceList.isInitialized) { "StockPriceList must by initialized !" }

        return this.chartProvider.getPngByteArray(
            chartTitle = getChartTitle(),
            priceList = stockPriceList,
            buyPoint = buyPoint,
            closePoint = closePoint
        )
    }

    companion object {
        fun create(chartProvider: ChartProvider, block: ChartBuilder.() -> Unit): ChartBuilder {
            val chartBuilder = ChartBuilder(chartProvider)
            chartBuilder.block()
            return chartBuilder
        }
    }

}
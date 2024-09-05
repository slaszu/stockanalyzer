package pl.slaszu.stockanalyzer.infrastructure.chart

import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.statistics.kandy.layers.candlestick
import org.jfree.chart.ChartUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider

@Service
class KotlinKandyChartProvider(val buildProperty: BuildProperties) : ChartProvider {
    override fun getPngByteArray(
        code: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint?,
        closePoint: ChartPoint?
    ): ByteArray {

        val xList = mutableListOf<kotlinx.datetime.LocalDate>()
        val openList = mutableListOf<Double>()
        val highList = mutableListOf<Double>()
        val lowList = mutableListOf<Double>()
        val closeList = mutableListOf<Double>()

        val priceListReversed = priceList.reversed()
        priceListReversed.forEach { stockPriceDto ->
            xList.add(stockPriceDto.date.toKotlinLocalDate())
            openList.add(stockPriceDto.priceOpen.toDouble())
            highList.add(stockPriceDto.priceHigh.toDouble())
            lowList.add(stockPriceDto.priceLow.toDouble())
            closeList.add(stockPriceDto.price.toDouble())
        }

        val plot = plot {
            candlestick(xList, openList, highList, lowList, closeList) {
                x {
                    axis.name = "Date"
                    axis.breaks(
                        format = "%d %b %y"
                    )
                }
            }
            layout {
                title = code
                caption = "#gpwApiSignals ver.${buildProperty.version}"
                size = 800 to 600
                style {
                    global.title {
                        //hJust = 100.0
                    }
                }
            }
        }

        val bufferedImage = plot.toBufferedImage()

        return ChartUtils.encodeAsPNG(bufferedImage)
    }

}
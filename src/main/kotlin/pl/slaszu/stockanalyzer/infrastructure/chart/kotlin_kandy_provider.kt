package pl.slaszu.stockanalyzer.infrastructure.chart

import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.internal.dataframe.DataFramePlotBuilder
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.LegendType
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol
import org.jetbrains.kotlinx.kandy.letsplot.style.LegendPosition
import org.jetbrains.kotlinx.kandy.util.color.Color
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
                    axis.name = ""
                    axis.breaks(
                        format = "%d %b %y"
                    )
                }
                decrease.borderLine.color = Color.RED
                increase.borderLine.color = Color.GREEN
                alpha = 0.8
            }

            addChartPoints(buyPoint, closePoint)

            layout {
                title = code
                caption = "#gpwApiSignals ver.${buildProperty.version}"
                size = 800 to 600
                style {
                    plotCanvas.caption {
                        color = Color.GREY
                    }
                    plotCanvas.title {
                        color = Color.BLACK
                        margin(3.0, 350.0)
                    }
                    legend.position = LegendPosition.Bottom
                    xAxis.line {
                        blank = true
                    }
                }
            }
        }

        val bufferedImage = plot.toBufferedImage()

        return ChartUtils.encodeAsPNG(bufferedImage)
    }
}

fun DataFramePlotBuilder<*>.addChartPoints(vararg pointsIn: ChartPoint?) {

    val points = pointsIn.filter { pointOne -> pointOne != null }

    if (points.isEmpty()) return;

    val pointX = mutableListOf<Long>()
    val pointY = mutableListOf<Double>()
    val pointType = mutableListOf<String>()
    val pointLabels = mutableListOf<String>()
    points.forEach { pointOne ->
        pointX.add(
            pointOne!!.point.date.toKotlinLocalDate().atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )
        pointY.add(
            pointOne!!.pointValue.toDouble()
        )
        pointLabels.add(
            pointOne!!.label
        )
        pointType.add(
            pointOne.let {
                if (it.label.contains("BUY")) {
                    "BUY"
                } else {
                    "SELL"
                }
            }
        )
    }


    points {
        x(pointX)
        y(pointY)

        symbol = Symbol.CROSS
        size = 6.0
        stroke = 2

        color(pointType) {
            scale = categorical(
                "BUY" to Color.BLACK, "SELL" to Color.BLUE
            )
            legend.name = ""
            legend.breaksLabeled("BUY" to pointLabels[0], "SELL" to "Miss")
            legend.type = LegendType.DiscreteLegend()
        }

    }

}
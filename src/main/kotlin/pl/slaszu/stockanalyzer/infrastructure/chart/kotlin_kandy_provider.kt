package pl.slaszu.stockanalyzer.infrastructure.chart

import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.internal.dataframe.DataFramePlotBuilder
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.lineRanges
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.LegendType
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol
import org.jetbrains.kotlinx.kandy.letsplot.style.LegendPosition
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.jetbrains.kotlinx.kandy.util.color.StandardColor
import org.jetbrains.kotlinx.statistics.kandy.layers.candlestick
import org.jfree.chart.ChartUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import java.time.LocalDate

@Service
class KotlinKandyChartProvider(val buildProperty: BuildProperties) : ChartProvider {
    override fun getPngByteArray(
        chartTitle: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint?,
        closePoint: ChartPoint?
    ): ByteArray {

        val xList = mutableListOf<kotlinx.datetime.LocalDate>()
        val openList = mutableListOf<Double>()
        val highList = mutableListOf<Double>()
        val lowList = mutableListOf<Double>()
        val closeList = mutableListOf<Double>()
        val volumeList = mutableListOf<Double>()

        val priceListReversed = priceList.reversed()
        priceListReversed.forEach { stockPriceDto ->
            xList.add(stockPriceDto.date.toKotlinLocalDate())
            openList.add(stockPriceDto.priceOpen.toDouble())
            highList.add(stockPriceDto.priceHigh.toDouble())
            lowList.add(stockPriceDto.priceLow.toDouble())
            closeList.add(stockPriceDto.price.toDouble())

            volumeList.add(stockPriceDto.volume.toDouble())
        }

        val maxPrice = highList.max()
        val minPrice = lowList.min()

        val maxVolume = volumeList.max()

        volumeList.replaceAll { v ->
            ((v * 100 / maxVolume) / 100 * (maxPrice-minPrice)) + minPrice
        }

        val plot = plot {

            // volumes
            lineRanges {
                x(xList)
                yMin.constant(minPrice)
                yMax(volumeList)
                borderLine.color = Color.GREY
                alpha = 0.6

            }

            candlestick(xList, openList, highList, lowList, closeList) {
                x {
                    axis.name = ""
                    axis.breaks(
                        format = "%d %b %y"
                    )
                }
                decrease.borderLine.color = Color.RED
                increase.borderLine.color = Color.GREEN
                alpha = 0.6
            }

            addChartPoints(buyPoint, closePoint)

            layout {
                title = chartTitle
                subtitle = buildSubtitle(buyPoint, closePoint)?.first
                caption = "#gpwApiSignals ver.${buildProperty.version}"
                size = 800 to 600
                style {
                    plotCanvas.caption {
                        color = Color.GREY
                    }
                    plotCanvas.title {
                        color = Color.BLACK
                        fontSize = 20.0
                    }
                    plotCanvas.subtitle {
                        color = buildSubtitle(buyPoint, closePoint)?.second
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
    val pointLabels = mutableMapOf<String, String>()
    val pointColour = mutableMapOf<String, StandardColor.Hex>()
    points.forEach { pointOne ->
        pointX.add(
            pointOne!!.point.date.toEpochMilliseconds()
        )
        pointY.add(
            pointOne.pointValue.toDouble()
        )

        val pointOneUnique = pointType.addAsUnique(pointOne.getType())
        pointLabels[pointOneUnique] = pointOne.label

        when (pointOne.getType()) {
            "BUY" -> pointColour.set(pointOneUnique, MyColor.GREEN.color)
            "SELL" -> pointColour.set(pointOneUnique, MyColor.RED.color)
            else -> pointColour.set(pointOneUnique, Color.GREY)
        }
    }


    points {
        x(pointX)
        y(pointY)

        symbol = Symbol.CROSS
        size = 7.0
        stroke = 2

        color(pointType) {
            scale = categorical(*pointColour.toList().toTypedArray())
            legend.name = ""
            legend.breaksLabeled(*pointLabels.toList().toTypedArray())
            legend.type = LegendType.DiscreteLegend()
        }
    }

}

enum class MyColor(val color: StandardColor.Hex) {
    RED(Color.hex("#910303")),
    GREEN(Color.hex("#0b5718"))
}

fun LocalDate.toEpochMilliseconds(): Long {
    return this.toKotlinLocalDate()
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun buildSubtitle(buyPoint: ChartPoint?, closePoint: ChartPoint?): Pair<String, StandardColor.Hex>? {
    if (buyPoint == null || closePoint == null) return null

    if (buyPoint.pointValue < closePoint.pointValue) {
        return closePoint.label to MyColor.GREEN.color
    }

    return closePoint.label to MyColor.RED.color
}

fun ChartPoint.getType(): String {
    if (this.label.contains("BUY")) {
        return "BUY"
    }
    return "SELL"
}

fun MutableList<String>.addAsUnique(e: String): String {
    val count = this.count { element -> element.contains(e) }
    var eToAdd = e;
    if (count > 0) {
        eToAdd = eToAdd.plus(" $count")
    }
    this.add(eToAdd)
    return eToAdd
}
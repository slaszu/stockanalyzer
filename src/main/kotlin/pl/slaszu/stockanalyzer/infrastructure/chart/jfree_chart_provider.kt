package pl.slaszu.stockanalyzer.infrastructure.chart

import org.jfree.chart.ChartUtils
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYPointerAnnotation
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.CandlestickRenderer
import org.jfree.chart.ui.TextAnchor
import org.jfree.data.xy.DefaultHighLowDataset
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.shared.toDate
import java.awt.Color
import java.awt.Font
import java.util.*


@Service
class JFreeChartProvider : ChartProvider {

    override fun getPngByteArray(
        code: String,
        priceList: Array<StockPriceDto>,
        buyPoint: ChartPoint?,
        closePoint: ChartPoint?
    ): ByteArray {


        val size: Int = priceList.size

        val dateArray = arrayOfNulls<Date>(size)
        val highArray = DoubleArray(size)
        val lowArray = DoubleArray(size)
        val openArray = DoubleArray(size)
        val closeArray = DoubleArray(size)
        val volumeArray = DoubleArray(size)

        val pointMap = mutableMapOf<ChartPoint, Int>()

        for (i in 0 until size) {
            val s = priceList[i]
            dateArray[i] = s.date.toDate()
            highArray[i] = s.priceHigh.toDouble()
            lowArray[i] = s.priceLow.toDouble()
            openArray[i] = s.priceOpen.toDouble()
            closeArray[i] = s.price.toDouble()
            volumeArray[i] = s.volume.toDouble()
            if (s == buyPoint?.point) {
                pointMap[buyPoint] = i
            }
            if (s == closePoint?.point) {
                pointMap[closePoint] = i
            }
        }

        val defaultHighLowDataset = DefaultHighLowDataset(
            code,
            dateArray,
            highArray,
            lowArray,
            openArray,
            closeArray,
            volumeArray
        )


        val plot = this.getJFreeChartPlot(defaultHighLowDataset)

        if (buyPoint != null && pointMap[buyPoint] != null) {

            plot.addAnnotation(
                getAnnotationPointer(
                    buyPoint.label,
                    defaultHighLowDataset.getXValue(0, pointMap[buyPoint]!!),
                    defaultHighLowDataset.getYValue(0, pointMap[buyPoint]!!)
                )
            )
        }

        if (closePoint != null && pointMap[closePoint] != null) {

            plot.addAnnotation(
                getAnnotationPointer(
                    closePoint.label,
                    defaultHighLowDataset.getXValue(0, pointMap[closePoint]!!),
                    defaultHighLowDataset.getYValue(0, pointMap[closePoint]!!)
                )
            )
        }


        val chart = JFreeChart(code, JFreeChart.DEFAULT_TITLE_FONT, plot, false);


        val bufferedImage = chart.createBufferedImage(800, 600)

        return ChartUtils.encodeAsPNG(bufferedImage)
    }

    private fun getJFreeChartPlot(dataset: DefaultHighLowDataset): XYPlot {

        val timeAxis = DateAxis("date");

        val valueAxis = NumberAxis("price");
        valueAxis.autoRangeIncludesZero = false;

        val plot = XYPlot(dataset, timeAxis, valueAxis, null);
        plot.renderer = CandlestickRenderer();

        return plot
    }

    private fun getAnnotationPointer(label: String, chartX: Double, chartY: Double): XYPointerAnnotation {
        val pointer = XYPointerAnnotation(
            label,
            chartX,
            chartY,
            Math.PI
        )
        pointer.setBaseRadius(90.0)
        pointer.setTipRadius(10.0)
        pointer.setFont(Font("SansSerif", Font.BOLD, 14))
        pointer.setPaint(Color.BLACK)
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT)
        return pointer;
    }
}
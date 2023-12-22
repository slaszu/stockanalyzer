package pl.slaszu.stockanalyzer.chart.application

import org.jfree.chart.ChartUtils
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.CandlestickRenderer
import org.jfree.data.xy.DefaultHighLowDataset
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import pl.slaszu.stockanalyzer.shared.toDate
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@Service
class ChartProvider {
    @OptIn(ExperimentalEncodingApi::class)
    fun getChartAsBase64(code:String, priceList: Array<StockPriceDto>): String {

        val size: Int = priceList.size

        val dateArray = arrayOfNulls<Date>(size)
        val highArray = DoubleArray(size)
        val lowArray = DoubleArray(size)
        val openArray = DoubleArray(size)
        val closeArray = DoubleArray(size)
        val volumeArray = DoubleArray(size)

        for (i in 0 until size) {
            val s = priceList[i]
            dateArray[i] = s.date.toDate()
            highArray[i] = s.priceHigh.toDouble()
            lowArray[i] = s.priceLow.toDouble()
            openArray[i] = s.priceOpen.toDouble()
            closeArray[i] = s.price.toDouble()
            volumeArray[i] = s.volume.toDouble()
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


        val chart: JFreeChart = this.getJFreeChart(defaultHighLowDataset, code)
        val bufferedImage = chart.createBufferedImage(800, 600)
        val bytes = ChartUtils.encodeAsPNG(bufferedImage)
        val encode: ByteArray = Base64.encodeToByteArray(bytes)

        return encode.decodeToString()
    }

    private fun getJFreeChart(dataset: DefaultHighLowDataset, code: String): JFreeChart {

        val timeAxis = DateAxis("date");

        val valueAxis = NumberAxis("price");
        valueAxis.autoRangeIncludesZero = false;

        val plot = XYPlot(dataset, timeAxis, valueAxis, null);
        plot.renderer = CandlestickRenderer();

        return JFreeChart(code, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

    }
}
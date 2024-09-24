package pl.slaszu.stockanalyzer.infrastructure.report

import com.samskivert.mustache.Mustache
import gui.ava.html.image.generator.HtmlImageGenerator
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import pl.slaszu.shared_kernel.domain.calcSellPrice
import pl.slaszu.shared_kernel.domain.roundTo
import pl.slaszu.shared_kernel.domain.toPngByteArray
import pl.slaszu.stockanalyzer.domain.report.ReportProvider
import java.io.File
import java.time.format.DateTimeFormatter

@Service
class MustacheReportProvider(
    private val closeAlertRepository: CloseAlertRepository,
    private val templateLoader: MustacheResourceTemplateLoader,
    private val compiler: Mustache.Compiler,
    private val buildProperties: BuildProperties
) : ReportProvider {
    override fun getHtml(closeAlertModelList: List<CloseAlertModel>, data: Map<String, String>?): String {

        val reader = templateLoader.getTemplate("report")
        val template = compiler.compile(reader)
        val html = template.execute(ReportContext(closeAlertModelList, buildProperties, data ?: mapOf()))

        File("testing.html").writeText(html)

        return html
    }

    override fun getPngByteArray(html: String): ByteArray {

        val imageGenerator = HtmlImageGenerator()

        imageGenerator.loadHtml(html)

        return imageGenerator.bufferedImage.toPngByteArray()
    }
}


class ReportContext(
    private val closeAlertModelList: List<CloseAlertModel>,
    private val build: BuildProperties,
    private var data: Map<String, String>
) {
    var rows: MutableList<Map<String, String>> = mutableListOf()

    init {
        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")

        val data = this.data.toMutableMap()
        data.putIfAbsent("summary", "0")
        data.putIfAbsent("days", "0")
        data["summary_class"] = if (this.data.getOrDefault("summary", "0").toFloat() > 0) "green" else "red"
        this.data = data.toMap()

        this.closeAlertModelList.sortedByDescending {
            it.resultPercent
        }.forEach {
            this.rows.add(
                mapOf(
                    "stock" to "${it.alert.stockName} [${it.alert.stockCode}]",
                    "buy_price" to "${it.alert.price}",
                    "buy_date" to it.alert.date.format(formatter),
                    "result" to "${it.resultPercent}",
                    "result_class" to if (it.resultPercent > 0) "green" else "red",
                    "days" to "${it.daysAfter}",
                    "sell_price" to "${it.price ?: calcSellPrice(it.alert.price, it.resultPercent).roundTo(2)}",
                    "sell_date" to it.date.format(formatter)
                )
            )
        }
    }
}

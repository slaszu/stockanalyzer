package pl.slaszu.stockanalyzer.infrastructure.report

import com.samskivert.mustache.Mustache
import gui.ava.html.image.generator.HtmlImageGenerator
import kotlinx.datetime.toKotlinLocalDateTime
import org.jfree.chart.ChartUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import pl.slaszu.stockanalyzer.domain.report.ReportProvider
import pl.slaszu.stockanalyzer.shared.calcSellPrice
import pl.slaszu.stockanalyzer.shared.roundTo
import java.io.File
import java.time.format.DateTimeFormatter

@Service
class MustacheReportProvider(
    private val closeAlertRepository: CloseAlertRepository,
    private val templateLoader: MustacheResourceTemplateLoader,
    private val compiler: Mustache.Compiler,
    private val buildProperties: BuildProperties
) : ReportProvider {
    override fun getHtml(alertList: List<AlertModel>): String {
        val alertsMap = mutableMapOf<AlertModel, List<CloseAlertModel>>()

        alertList.forEach {
            val closeAlertForIt = this.closeAlertRepository.findByAlertId(it.id!!)

            alertsMap[it] = closeAlertForIt.sortedBy { closeAlert -> closeAlert.daysAfter }

        }

        val reader = templateLoader.getTemplate("report")
        val template = compiler.compile(reader)
        val html = template.execute(ReportContext(alertsMap, buildProperties))

        File("testing.html").writeText(html)

        return html
    }

    override fun getPngByteArray(alertList: List<AlertModel>): ByteArray {

        val imageGenerator = HtmlImageGenerator()

        imageGenerator.loadHtml(this.getHtml(alertList))

        return ChartUtils.encodeAsPNG(imageGenerator.bufferedImage)
    }
}


class ReportContext(
    private val alertMap: Map<AlertModel, List<CloseAlertModel>>,
    private val build: BuildProperties
) {
    var alerts: List<Pair<Map<String, String>, List<Map<String, String>>>> = emptyList()

    init {
        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")
        val alertMap = mutableMapOf<Map<String, String>, List<Map<String, String>>>()
        val closeList = mutableListOf<Map<String, String>>()
        this.alertMap.forEach { (a, c) ->
            c.forEach {
                closeList.add(
                    mapOf(
                        "result" to "${it.resultPercent}",
                        "result_class" to if (it.resultPercent > 0) "green" else "red",
                        "days" to "${it.daysAfter}",
                        "sell_price" to "${it.price ?: calcSellPrice(a.price, it.resultPercent).roundTo(2)}",
                        "sell_date" to it.date.format(formatter)
                    )
                )
            }
            alertMap.put(
                mapOf(
                    "stock" to "${a.stockName} [${a.stockCode}]",
                    "buy_price" to "${a.price}",
                    "buy_date" to a.date.format(formatter)
                ),
                closeList.toList()
            )
            closeList.clear()
        }

        this.alerts = alertMap.toList()
    }
}

package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.shared.roundTo
import java.time.LocalDateTime


@Service
class CreateReport(
    private val closeAlertModelRepo: CloseAlertRepository,
    private val chartForAlert: ChartForAlert,
    private val publisher: Publisher,
    private val logger: KLogger = KotlinLogging.logger { }
) {

    fun runForDaysAfter(daysAfter: Int) {

        val date = LocalDateTime.now().minusDays(daysAfter.toLong())
        val closedAlertModelList = this.closeAlertModelRepo.findCloseAlertsAfterDate(date)

        this.logger.debug { "Found ${closedAlertModelList.size} alert closed for date $date" }

        if (closedAlertModelList.isEmpty()) {
            return
        }

        val summaryPercent = this.getSummaryPercent(closedAlertModelList)

        val topList = this.getTopDesc(closedAlertModelList)
        val worstList = this.getLastDesc(closedAlertModelList)

        val topCharts: MutableList<ByteArray> = mutableListOf()
        topList.forEach { closeAlertModel ->
            val chart = this.chartForAlert.getChartPngForCloseAlert(closeAlertModel) ?: return@forEach
            topCharts.add(chart)
        }

        val worstCharts: MutableList<ByteArray> = mutableListOf()
        worstList.forEach { closeAlertModel ->
            val chart = this.chartForAlert.getChartPngForCloseAlert(closeAlertModel) ?: return@forEach
            worstCharts.add(chart)
        }

//        val html = this.reportProvider.getHtml(closedAlertModelList, mapOf(
//            "days" to daysAfter.toString(),
//            "summary" to summaryPercent.toString()
//        ))
//        val pngByteArray = this.reportProvider.getPngByteArray(html)

        this.publisher.publish(
            topCharts.plus(worstCharts),
            "Podsumowanie (last $daysAfter days)\n" +
                    "‼\uFE0FWynik $summaryPercent %",

            "\uD83D\uDFE9Najlepsze:\n" + topList.joinToString("\n") {
                "${it.alert.stockName} [#${it.alert.stockCode}] +${it.resultPercent} %"
            } + "\n" +
                    "\uD83D\uDFE5Najgorsze:\n" + worstList.joinToString("\n") {
                "${it.alert.stockName} [#${it.alert.stockCode}] ${it.resultPercent} %"
            } + "\n#gpwApiSignals"
        )
    }

    private fun getSummaryPercent(closeAlertsList: List<CloseAlertModel>): Float {
        return closeAlertsList.sumOf { it.resultPercent.toDouble() }.toFloat().roundTo(2)
    }

    private fun getTopDesc(closeAlertsList: List<CloseAlertModel>): List<CloseAlertModel> {
        // sortuj malejaco
        // tylko dodatnie zwoty
        // max 3
        return closeAlertsList.filter { it.resultPercent > 0 }.sortedByDescending { it.resultPercent }.take(3)
    }

    private fun getLastDesc(closeAlertsList: List<CloseAlertModel>): List<CloseAlertModel> {
        // sortuj rosnaco
        // tylko ujemne zwroty
        // max 3
        return closeAlertsList.filter { it.resultPercent < 0 }.sortedBy { it.resultPercent }.take(3)
    }
}
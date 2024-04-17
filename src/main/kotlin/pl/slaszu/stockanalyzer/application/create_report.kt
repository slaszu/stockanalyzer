package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.report.ReportProvider
import pl.slaszu.stockanalyzer.shared.roundTo
import java.time.LocalDateTime


@Service
class CreateReport(
    private val closeAlertModelRepo: CloseAlertRepository,
    private val reportProvider: ReportProvider,
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

        val html = this.reportProvider.getHtml(closedAlertModelList, mapOf(
            "days" to daysAfter.toString(),
            "summary" to summaryPercent.toString()
        ))
        val pngByteArray = this.reportProvider.getPngByteArray(html)

        this.publisher.publish(
            pngByteArray,
            "Podsumowanie (last $daysAfter days)\n" +
                    "Wynik $summaryPercent %",
            "${this.getTopDesc(closedAlertModelList)}\n" +
                    "${this.getLastDesc(closedAlertModelList)}\n" +
                    "#gpwApiSignals"
        )
    }

    private fun getSummaryPercent(closeAlertsList: List<CloseAlertModel>): Float {
        return closeAlertsList.sumOf { it.resultPercent.toDouble() }.toFloat().roundTo(2)
    }

    private fun getTopDesc(closeAlertsList: List<CloseAlertModel>): String {
        // sortuj malejaco
        // tylko dodatnie zwoty
        // max 3
        val res = closeAlertsList.filter { it.resultPercent > 0 }.sortedByDescending { it.resultPercent }.take(3)
        if (res.isEmpty()) {
            return "";
        }

        return "Najlepsze:\n" + res.joinToString("\n") {
            "${it.alert.stockName} [#${it.alert.stockCode}] +${it.resultPercent} % (after ${it.daysAfter} days)"
        }
    }

    private fun getLastDesc(closeAlertsList: List<CloseAlertModel>): String {
        // sortuj malejaco
        // tylko ujemne zwroty
        // max 3
        val res = closeAlertsList.filter { it.resultPercent < 0 }.sortedByDescending { it.resultPercent }.takeLast(3)
        if (res.isEmpty()) {
            return "";
        }

        return "Najgorsze:\n" + res.joinToString("\n") {
            "${it.alert.stockName} [#${it.alert.stockCode}] ${it.resultPercent} % (after ${it.daysAfter} days)"
        }
    }
}
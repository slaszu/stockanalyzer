package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.CloseAlertProvider
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import pl.slaszu.stockanalyzer.domain.report.ReportProvider
import java.time.LocalDateTime


@Service
class CreateReport(
    private val alertRepository: AlertRepository,
    private val reportProvider: ReportProvider,
    private val publisher: Publisher,
    private val closeAlertProvider: CloseAlertProvider,
    private val logger: KLogger = KotlinLogging.logger { }
) {

    fun runForDaysAfter(daysAfter: Int) {

        /**
         * 1. get alert closed and
         */
        val date = LocalDateTime.now().minusDays(daysAfter.toLong())
        val alertClosedList = this.alertRepository.findAlertsClosedAfterThatDate(date)

        this.logger.debug { "Found ${alertClosedList.size} alert closed for date $date" }

//        val html = this.reportProvider.getHtml(alertClosedList);
//        println(html)

        val pngByteArray = this.reportProvider.getPngByteArray(alertClosedList)

        val closeAlertsList = this.closeAlertProvider.getAllForAlerts(alertClosedList)

        this.publisher.publish(
            pngByteArray,
            "Podsumowanie (last $daysAfter days)",
            "${this.getTopDesc(closeAlertsList)}\n${this.getLastDesc(closeAlertsList)}"
        )
    }

    private fun getTopDesc(closeAlertsList: List<CloseAlertModel>): String
    {
        // sortuj malejaco
        // tylko dodatnie zwoty
        // max 3
        val res = closeAlertsList.filter { it.resultPercent > 0 }.sortedByDescending { it.resultPercent }.take(3)
        if (res.isEmpty()) {
            return "";
        }

        return "Best:\n" + res.joinToString("\n") {
            "${it.alert.stockName} [${it.alert.stockCode}] +${it.resultPercent} % (after ${it.daysAfter} days)"
        }
    }

    private fun getLastDesc(closeAlertsList: List<CloseAlertModel>): String
    {
        // sortuj malejaco
        // tylko ujemne zwroty
        // max 3
        val res = closeAlertsList.filter { it.resultPercent < 0 }.sortedByDescending { it.resultPercent }.takeLast(3)
        if (res.isEmpty()) {
            return "";
        }

        return "Worst:\n" + res.joinToString("\n") {
            "${it.alert.stockName} [${it.alert.stockCode}] ${it.resultPercent} % (after ${it.daysAfter} days)"
        }
    }
}
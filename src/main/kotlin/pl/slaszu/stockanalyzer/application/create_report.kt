package pl.slaszu.stockanalyzer.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import pl.slaszu.stockanalyzer.domain.report.ReportProvider
import java.time.LocalDateTime


@Service
class CreateReport(
    private val alertRepository: AlertRepository,
    private val reportProvider: ReportProvider,
    private val logger: KLogger = KotlinLogging.logger { }
) {

    fun runForDaysAfter(daysAfter: Int) {

        /**
         * 1. get alert closed and
         */
        val date = LocalDateTime.now().minusDays(daysAfter.toLong())
        val alertClosedList = this.alertRepository.findByCloseDateAfterAndCloseIsTrue(date)

        this.logger.debug { "Found ${alertClosedList.size} alert closed for date $date" }

        val html = this.reportProvider.getHtml(alertClosedList);

        println(html)

        //this.reportProvider.getPngByteArray(alertClosedList)

    }
}
package pl.slaszu.stockanalyzer.application

import com.samskivert.mustache.Mustache
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import java.time.LocalDateTime

@Service
class CreateReport(
    private val closeAlertRepository: CloseAlertRepository,
    private val alertRepository: AlertRepository,
    private val templateLoader: MustacheResourceTemplateLoader,
    private val compiler: Mustache.Compiler,
    private val logger: KLogger = KotlinLogging.logger { }
) {

    fun runForDaysAfter(daysAfter: Int) {

        /**
         * 1. get alert closed and
         */
        val date = LocalDateTime.now().minusDays(daysAfter.toLong())
        val alertClosedList = this.alertRepository.findByCloseDateAfterAndCloseIsTrue(date)

        this.logger.debug { "Found ${alertClosedList.size} alert closed for date $date" }

        val alertsMap = mutableMapOf<AlertModel,List<CloseAlertModel>>()

        alertClosedList.forEach {
            val closeAlertForIt = this.closeAlertRepository.findByAlertId(it.id!!)

            alertsMap[it] = closeAlertForIt.sortedBy { closeAlert -> closeAlert.daysAfter }

        }

//        alertsMap.toList().forEach {
//            println(it.first)
//            println(it.second)
//
//        }

        val reader = templateLoader.getTemplate("report")
        val template = compiler.compile(reader)
        val html = template.execute(object {
            val alerts = alertsMap.toList()
        })

        // todo html to image https://github.com/danfickle/openhtmltopdf/wiki/Java2D-Image-Output

        println(html)
    }
}

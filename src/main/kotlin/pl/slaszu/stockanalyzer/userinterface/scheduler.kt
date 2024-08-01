package pl.slaszu.stockanalyzer.userinterface

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.application.CloseAlerts
import pl.slaszu.stockanalyzer.application.CreateAlerts
import pl.slaszu.stockanalyzer.application.CreateReport

val logger = KotlinLogging.logger { }

@Service
@Profile("prod")
class Scheduler(
    val createAlerts: CreateAlerts,
    val closeAlerts: CloseAlerts,
    val createReport: CreateReport
) {

    @Scheduled(cron = "0 * * * * *")
    @Profile("default")
    fun runTest() {
        logger.info { "Scheduler:runTest do nothing" }
    }

    @Scheduled(cron = "30 0,15,30,45 9-17 * * MON-FRI")
    fun runCreateAlert() {
        logger.info { "Scheduler:runCreateAlert" }
        this.createAlerts.run()
    }

    @Scheduled(cron = "45 5,20,35,50 9-17 * * MON-FRI")
    fun runCheckAlerts7() {
        logger.info { "Scheduler:runCheckAlerts 7 andClose=false" }
        this.closeAlerts.runForDaysAfter(7)
    }

    @Scheduled(cron = "55 10,25,40,55 9-17 * * MON-FRI")
    fun runCheckAlerts14() {
        logger.info { "Scheduler:runCheckAndCloseAlerts 14 andClose=false" }
        this.closeAlerts.runForDaysAfter(14)
    }

    @Scheduled(cron = "55 10,25,40,55 9-17 * * MON-FRI")
    fun runCheckAlerts30AndClose() {
        logger.info { "Scheduler:runCheckAndCloseAlerts 30 andClose=true" }
        this.closeAlerts.runForDaysAfter(30, true)
    }

    @Scheduled(cron = "0 0 18 * * SUN")
    fun runCreateWeekReport() {
        logger.info { "Scheduler:runCreateWeekReport 7" }
        this.createReport.runForDaysAfter(7)
    }
}

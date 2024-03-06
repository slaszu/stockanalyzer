package pl.slaszu.stockanalyzer.userinterface

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.application.CloseAlerts
import pl.slaszu.stockanalyzer.application.CreateAlerts

val logger = KotlinLogging.logger { }

@Service
class Scheduler(val createAlerts: CreateAlerts, val closeAlerts: CloseAlerts) {

    @Scheduled(cron = "0 * * * * *")
    @Profile("default")
    fun runTest() {
        logger.info { "Scheduler:runTest do nothing" }
    }

    @Scheduled(cron = "30 */15 9-17 * * MON-FRI")
    fun runCreateAlert() {
        logger.info { "Scheduler:runCreateAlert" }
        this.createAlerts.run()
    }

    @Scheduled(cron = "45 */15 9-17 * * MON-FRI")
    fun runCheckAlerts() {
        logger.info { "Scheduler:runCheckAlerts 7 andClose=false" }
        this.closeAlerts.runForDaysAfter(7)
    }

    @Scheduled(cron = "55 */15 9-17 * * MON-FRI")
    fun runCheckAndCloseAlerts() {
        logger.info { "Scheduler:runCheckAndCloseAlerts 14 andClose=true" }
        this.closeAlerts.runForDaysAfter(14, true)
    }
}

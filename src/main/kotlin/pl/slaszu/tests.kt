package pl.slaszu

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.application.CreateAlerts


@Configuration
class LocalTest(
    private val createAlert: CreateAlerts
) {
    private val logger = KotlinLogging.logger { }

    @Bean
    fun createAlert(): ApplicationRunner = ApplicationRunner { createAlert.run() }
}

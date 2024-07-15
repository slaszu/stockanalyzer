package pl.slaszu

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.stockanalyzer.application.CreateAlerts


@Configuration
class LocalTest(
    private val createAlert: CreateAlerts,
    val blogClient: BlogClient
) {
    private val logger = KotlinLogging.logger { }

    //@Bean
    fun createAlert(): ApplicationRunner = ApplicationRunner { createAlert.run() }

    @Bean
    fun getPosts(): ApplicationRunner = ApplicationRunner {
        //this.blogClient.getPosts()
        this.blogClient.insertPost(AlertModel("PLW", "Playway", 300f,
            date = LocalDateTime(2024, 7, 5, 12, 0, 0, 0).toJavaLocalDateTime()
        ))
    }
}

//package pl.slaszu
//
//import io.github.oshai.kotlinlogging.KotlinLogging
//import io.sentry.Sentry
//import kotlinx.datetime.LocalDateTime
//import kotlinx.datetime.toJavaLocalDateTime
//import org.springframework.boot.ApplicationRunner
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import pl.slaszu.blog.application.BlogPostForAlert
//import pl.slaszu.blog.domain.BlogClient
//import pl.slaszu.recommendation.application.RecommendationForAlert
//import pl.slaszu.shared_kernel.domain.alert.AlertModel
//import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
//import pl.slaszu.shared_kernel.domain.toFile
//import pl.slaszu.stockanalyzer.application.ChartForAlert
//import pl.slaszu.stockanalyzer.application.CloseAlerts
//import pl.slaszu.stockanalyzer.application.CreateAlerts
//import kotlin.math.log
//import kotlin.random.Random
//
//
//@Configuration
//@Profile("default")
//class LocalTest(
//    private val createAlert: CreateAlerts,
//    private val closeAlerts: CloseAlerts,
//    private val blogPostForAlert: BlogPostForAlert,
//    private val recommendationForAlert: RecommendationForAlert,
//    val blogClient: BlogClient
//) {
//    private val logger = KotlinLogging.logger { }
//
//    //@Bean
//    fun testSentry(): ApplicationRunner = ApplicationRunner {
//        try {
//            throw Exception("This is a test ${Math.random()}")
//        } catch (e: Exception) {
//            Sentry.captureException(e)
//            logger.debug { "Sentry exception should by logged: ${e.message}" }
//        }
//    }
//
//    //@Bean
//    fun createBlogPost(): ApplicationRunner = ApplicationRunner {
//        var alert = AlertModel(
//            "KGH", "KGHM", 130.30f,
//            date = LocalDateTime(2024, 4, 9, 12, 0, 0, 0).toJavaLocalDateTime()
//        )
//
//        alert = alert.copy(
//            predictions = recommendationForAlert.getPredictionsMap(alert)
//        )
//
//        val url = blogPostForAlert.createNewPost(alert)
//
//        logger.debug { url }
//    }
//
//    //@Bean
//    fun createAlert(): ApplicationRunner = ApplicationRunner { createAlert.run() }
//
//    //@Bean
//    fun closeAlert(): ApplicationRunner = ApplicationRunner { closeAlerts.runForDaysAfter(7) }
//
//    //@Bean
//    fun kandy(chartForAlert: ChartForAlert): ApplicationRunner = ApplicationRunner {
//
//        var alert = AlertModel(
//            "KGH", "KGHM", 130.30f,
//            date = LocalDateTime(2024, 4, 9, 12, 0, 0, 0).toJavaLocalDateTime()
//        )
//
//        //val pngByteArray = chartForAlert.getChartPngForAlert(alert)
//
//        val closeAlert = CloseAlertModel(
//            alert = alert,
//            resultPercent = 5f,
//            daysAfter = 7,
//            price = 140.54f,
//            date = LocalDateTime(2024, 4, 16, 12, 0, 0, 0).toJavaLocalDateTime()
//
//        )
//        val pngByteArray = chartForAlert.getChartPngForCloseAlert(closeAlert)
//
//        val path = pngByteArray!!.toFile(
//            "test_chart_%.png".replace("%", Random.nextInt(from = 100, until = 999).toString())
//        )
//
//        this.logger.debug { path }
//    }
//}
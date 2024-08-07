//package pl.slaszu
//
//import io.github.oshai.kotlinlogging.KotlinLogging
//import kotlinx.datetime.LocalDateTime
//import kotlinx.datetime.toJavaLocalDateTime
//import org.springframework.boot.ApplicationRunner
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import pl.slaszu.blog.application.BlogPostForAlert
//import pl.slaszu.blog.domain.BlogClient
//import pl.slaszu.recommendation.application.RecommendationForAlert
//import pl.slaszu.shared_kernel.domain.alert.AlertModel
//import pl.slaszu.stockanalyzer.application.CreateAlerts
//
//
//@Configuration
//class LocalTest(
//    private val createAlert: CreateAlerts,
//    val blogClient: BlogClient
//) {
//    private val logger = KotlinLogging.logger { }
//
//    @Bean
//    fun createAlert(): ApplicationRunner = ApplicationRunner { createAlert.run() }
//
//    //@Bean
//    fun getPosts(
//        blogPostForAlert: BlogPostForAlert,
//        recommendationForAlert: RecommendationForAlert
//    ): ApplicationRunner = ApplicationRunner {
//
////        var alert = AlertModel(
////            "PLW", "Playway", 300f,
////            date = LocalDateTime(2024, 8, 6, 12, 0, 0, 0).toJavaLocalDateTime()
////        )
//
//        var alert = AlertModel(
//            "KGH", "KGHM", 130.30f,
//            date = LocalDateTime(2024, 4, 9, 12, 0, 0, 0).toJavaLocalDateTime()
//        )
//
//
//        alert = alert.copy(
//            predictions = recommendationForAlert.getPredictionsMap(alert)
//        )
//
//
//        blogPostForAlert.createNewPost(alert)
//    }
//}

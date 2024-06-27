//package pl.slaszu
//
//import io.github.oshai.kotlinlogging.KotlinLogging
//import io.qdrant.client.QdrantClient
//import kotlinx.datetime.LocalDate
//import org.springframework.boot.ApplicationRunner
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import pl.slaszu.recommendation.domain.RecommendationService
//import pl.slaszu.recommendation.domain.SimilarAlertSearchService
//import pl.slaszu.shared_kernel.domain.alert.AlertRepository
//
//
//@Configuration
//@Profile("default")
//class LocalTest {
//    private val logger = KotlinLogging.logger { }
//
//    //@Bean
//    fun searchForAlert(
//        client: QdrantClient,
//        config: QdrantClient,
//        alertRepository: AlertRepository,
//        similarAlertSearchService: SimilarAlertSearchService,
//        recommendationService: RecommendationService
//    ): ApplicationRunner {
//
//        return ApplicationRunner {
//            val result = similarAlertSearchService.searchBestFit(
//                "OPN",
//                LocalDate(2024, 5, 29)
//            )
//
//            result.forEach {
//                //logger.debug { "BestFit : $it" }
//            }
//
//            val reco = recommendationService.convertToRecommendation(result)
//            logger.debug { "${reco.getDaysAfterToResultAvg()}" }
//
//        }
//    }
//}

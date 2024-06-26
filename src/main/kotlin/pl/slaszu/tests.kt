//package pl.slaszu
//
//import io.qdrant.client.QdrantClient
//import io.qdrant.client.grpc.Collections
//import io.qdrant.client.grpc.Collections.VectorParams
//import kotlinx.datetime.LocalDate
//import org.springframework.boot.ApplicationRunner
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import pl.slaszu.recommendation.domain.RecommendationPersistService
//import pl.slaszu.recommendation.domain.RecommendationService
//import pl.slaszu.recommendation.domain.SimilarAlertSearchService
//import pl.slaszu.recommendation.domain.StockVector
//import pl.slaszu.recommendation.infrastructure.QdrantConfig
//import pl.slaszu.recommendation.infrastructure.logger
//import pl.slaszu.shared_kernel.domain.alert.AlertRepository
//
//
//@Configuration
//@Profile("default")
//class LocalTest {
//    //@Bean
//    fun initCollection(
//        config: QdrantConfig,
//        client: QdrantClient,
//        recommendationService: RecommendationPersistService,
//        alertRepository: AlertRepository
//    ): ApplicationRunner {
//        logger.debug { config.toString() }
//
//        // todo add some way to force recreate qdrant collection, matby api, or cli action ?!
//        return ApplicationRunner {
//            client.deleteCollectionAsync(config.collectionName).get();
//
//            client.collectionExistsAsync(config.collectionName)
//                .get().also {
//                    logger.debug { "${config.collectionName} exists = $it" }
//                    if (!it.equals(true)) {
//                        client.createCollectionAsync(
//                            config.collectionName,
//                            mapOf(
//                                "price" to VectorParams.newBuilder()
//                                    .setDistance(Collections.Distance.Cosine)
//                                    .setSize(StockVector.VECTOR_SIZE.toLong())
//                                    .build(),
//                                "volume" to VectorParams.newBuilder()
//                                    .setDistance(Collections.Distance.Cosine)
//                                    .setSize(StockVector.VECTOR_SIZE.toLong())
//                                    .build()
//                            )
//                        ).get().also {
//                            logger.debug { "${config.collectionName} create result = $it" }
//                        }
//                    }
//                }
//
//            alertRepository.findAll(
//                PageRequest.of(0, 100, Sort.by("date").descending())
//            ).forEach {
//                logger.debug { "$it" }
//                recommendationService.save(it)
//            }
//
//        }
//    }
//
//    @Bean
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

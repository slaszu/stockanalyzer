package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.grpc.Collections
import io.qdrant.client.grpc.Collections.VectorParams
import kotlinx.datetime.LocalDate
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPersistService
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationService
import pl.slaszu.stockanalyzer.domain.recommendation.SimilarAlertSearchService
import pl.slaszu.stockanalyzer.domain.recommendation.StockVector

val logger = KotlinLogging.logger { }

@ConfigurationProperties(prefix = "qdrant")
data class QdrantConfig(
    val apiKey: String,
    val collectionName: String
)

@Configuration
class QdrantBeans {

    @Bean
    fun getQdrantClient(config: QdrantConfig): QdrantClient {
        return QdrantClient(
            QdrantGrpcClient.newBuilder(
                "6a3b07e9-182c-48e3-92ce-7fb8c1d89577.europe-west3-0.gcp.cloud.qdrant.io",
                6334,
                true
            )
                .withApiKey(config.apiKey)
                .build()
        )
    }

    //@Bean
    fun initCollection(
        config: QdrantConfig,
        client: QdrantClient,
        recommendationService: RecommendationPersistService,
        alertRepository: AlertRepository
    ): ApplicationRunner {
        logger.debug { config.toString() }


        return ApplicationRunner {
            client.deleteCollectionAsync(config.collectionName).get();

            client.collectionExistsAsync(config.collectionName)
                .get().also {
                    logger.debug { "${config.collectionName} exists = $it" }
                    if (!it.equals(true)) {
                        client.createCollectionAsync(
                            config.collectionName,
                            mapOf(
                                "price" to VectorParams.newBuilder()
                                    .setDistance(Collections.Distance.Cosine)
                                    .setSize(StockVector.VECTOR_SIZE.toLong())
                                    .build(),
                                "volume" to VectorParams.newBuilder()
                                    .setDistance(Collections.Distance.Cosine)
                                    .setSize(StockVector.VECTOR_SIZE.toLong())
                                    .build()
                            )
                        ).get().also {
                            logger.debug { "${config.collectionName} create result = $it" }
                        }
                    }
                }

            alertRepository.findAll(
                PageRequest.of(0, 100, Sort.by("date").descending())
            ).forEach {
                logger.debug { "$it" }
                recommendationService.save(it)
            }

        }
    }

    @Bean
    fun searchForAlert(
        client: QdrantClient,
        config: QdrantClient,
        alertRepository: AlertRepository,
        similarAlertSearchService: SimilarAlertSearchService,
        recommendationService: RecommendationService
    ): ApplicationRunner {

        return ApplicationRunner {
            val result = similarAlertSearchService.searchBestFit(
                "CLC",
                LocalDate(2024, 5, 31)
            )

            result.forEach {
                //logger.debug { "BestFit : $it" }
            }

            val reco = recommendationService.convertToRecommendation(result)
            logger.debug { "${reco.getDaysAfterToResultAvg()}" }

        }
    }
}

package pl.slaszu.recommendation.infrastructure

import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
}

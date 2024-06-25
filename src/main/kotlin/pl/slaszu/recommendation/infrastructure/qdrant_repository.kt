package pl.slaszu.recommendation.infrastructure

import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.QdrantClient
import io.qdrant.client.VectorsFactory.namedVectors
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.grpc.Points.PointStruct
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationPayload
import pl.slaszu.recommendation.domain.SaveRepository
import pl.slaszu.recommendation.domain.StockVector

@Service
class QdrantSaveRepository(
    private val qdrantClient: QdrantClient,
    private val qdrantConfig: QdrantConfig,
) : SaveRepository {

    override fun save(vector: StockVector, payload: RecommendationPayload) {

        if (!vector.hasValidSize()) {
            return;
        }

        val point = PointStruct.newBuilder()
            .setId(id(payload.alertTweetId.toLong()))
            .setVectors(
                namedVectors(
                    mapOf(
                        "price" to vectors(vector.priceVector.toList()).vector,
                        "volume" to vectors(vector.volumeVector.toList()).vector
                    )
                )
            )
            .putAllPayload(payload.toQdrantPayload())
            .build()

        this.qdrantClient.upsertAsync(
            this.qdrantConfig.collectionName,
            listOf(point)
        )
    }
}

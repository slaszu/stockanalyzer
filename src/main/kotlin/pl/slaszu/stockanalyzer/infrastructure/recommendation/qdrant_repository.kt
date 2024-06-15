package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.QdrantClient
import io.qdrant.client.VectorsFactory.namedVectors
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.grpc.Points.PointStruct
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.recommendation.SaveRepository
import pl.slaszu.stockanalyzer.domain.recommendation.StockVector

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
            .setId(id(payload.closeAlertTweetId.toLong()))
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

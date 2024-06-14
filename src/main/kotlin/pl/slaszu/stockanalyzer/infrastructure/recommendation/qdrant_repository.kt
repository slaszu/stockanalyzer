package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.QdrantClient
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.grpc.Points.PointStruct
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.recommendation.SaveRepository

@Service
class QdrantSaveRepository(
    private val qdrantClient: QdrantClient,
    private val qdrantConfig: QdrantConfig,
    private val vectorConvert: VectorConvert
) : SaveRepository {
    override fun save(closeAlert: CloseAlertModel) {

        val vector = this.vectorConvert.create1DTensor(closeAlert.alert)

        if (vector.size < TensorDimensions.TENSOR_SIZE.size) {
            return;
        }

        val payload = RecommendationPayload.fromCloseAlert(closeAlert)

        val point = PointStruct.newBuilder()
            .setId(id(closeAlert.alert.tweetId.toLong()))
            .setVectors(vectors(vector.toList()))
            .putAllPayload(payload.toQdrantPayload())
            .build()

        this.qdrantClient.upsertAsync(
            this.qdrantConfig.collectionName,
            listOf(point)
        )
    }
}

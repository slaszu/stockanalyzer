package pl.slaszu.recommendation.infrastructure

import io.github.oshai.kotlinlogging.KotlinLogging
import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.QdrantClient
import io.qdrant.client.VectorsFactory.namedVectors
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.grpc.Collections
import io.qdrant.client.grpc.Collections.VectorParams
import io.qdrant.client.grpc.Points.PointStruct
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationPayload
import pl.slaszu.recommendation.domain.RecommendationRepository
import pl.slaszu.recommendation.domain.StockVector

@Service
class QdrantSaveRepository(
    private val qdrantClient: QdrantClient,
    private val qdrantConfig: QdrantConfig,
) : RecommendationRepository {

    private val logger = KotlinLogging.logger { }

    override fun save(vector: StockVector, payload: RecommendationPayload) {

        if (!vector.hasValidSize()) {
            return;
        }

        if (payload.getId() == null) {
            return;
        }

        val point = PointStruct.newBuilder()
            .setId(id(payload.getId()!!.toLong()))
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

    override fun createIfNotExists(): Boolean {
        return this.qdrantClient.collectionExistsAsync(this.qdrantConfig.collectionName)
            .get().also {
                this.logger.debug { "${this.qdrantConfig.collectionName} exists = $it" }
                if (!it.equals(true)) {
                    this.qdrantClient.createCollectionAsync(
                        this.qdrantConfig.collectionName,
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
                        this.logger.debug { "${this.qdrantConfig.collectionName} create result = $it" }
                    }
                }
            }
    }
}

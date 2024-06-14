package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.QdrantClient
import io.qdrant.client.grpc.Points
import io.qdrant.client.grpc.Points.WithPayloadSelector
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.recommendation.Search
import pl.slaszu.stockanalyzer.domain.recommendation.SearchResult

@Service
class QdrantSearchService(
    val client: QdrantClient,
    val config: QdrantConfig,
    val vectorConvert: VectorConvert
) : Search {
    override fun search(alertModel: AlertModel): List<SearchResult> {
        val vector = this.vectorConvert.create1DTensor(alertModel)

        val searchResults = client
            .searchAsync(
                Points.SearchPoints.newBuilder()
                    .setCollectionName(config.collectionName)
                    .addAllVector(vector.toMutableList())
                    .setLimit(6)
                    .setWithPayload(WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build()
            )
            .get();

        val result = mutableListOf<SearchResult>()
        searchResults.forEach {
            val payload = it.payloadMap
            result.add(
                SearchResult(
                    score = it.score,
                    payload = RecommendationPayload(
                        payload["stockCode"]?.stringValue ?: "_payload_missing_",
                        payload["result"]?.stringValue?.toFloat() ?: 0f,
                        payload["days"]?.stringValue?.toInt() ?: 0,
                        payload["closeAlertId"]?.stringValue ?: "_payload_missing_",
                        payload["tweetId"]?.stringValue ?: "_payload_missing_"
                    )
                )
            )
        }

        return result.toList()
    }

}
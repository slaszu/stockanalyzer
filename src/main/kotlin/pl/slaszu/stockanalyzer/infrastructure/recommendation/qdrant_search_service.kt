package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.QdrantClient
import io.qdrant.client.grpc.Points
import io.qdrant.client.grpc.Points.ScoredPoint
import io.qdrant.client.grpc.Points.SearchPoints
import io.qdrant.client.grpc.Points.WithPayloadSelector
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.recommendation.Search
import pl.slaszu.stockanalyzer.domain.recommendation.SearchResult
import pl.slaszu.stockanalyzer.domain.recommendation.StockVector

@Service
class QdrantSearch(
    val client: QdrantClient,
    val config: QdrantConfig
) : Search {

    override fun searchByPrice(stockVector: StockVector): List<SearchResult> {

        val searchResults = client
            .searchAsync(
                SearchPoints.newBuilder()
                    .setCollectionName(config.collectionName)
                    .setVectorName("price")
                    .addAllVector(stockVector.priceVector.toMutableList())
                    .setLimit(20)
                    //.setScoreThreshold(0.9f)
                    .setWithPayload(WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build()
            )
            .get();

        return searchResults.toSearchResultList()
    }

    override fun searchByVolume(stockVector: StockVector): List<SearchResult> {

        val searchResults = client
            .searchAsync(
                SearchPoints.newBuilder()
                    .setCollectionName(config.collectionName)
                    .setVectorName("volume")
                    .addAllVector(stockVector.volumeVector.toMutableList())
                    .setLimit(20)
                    //.setScoreThreshold(0.7f)
                    .setWithPayload(WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build()
            )
            .get();

        return searchResults.toSearchResultList()
    }

}

fun List<ScoredPoint>.toSearchResultList(): List<SearchResult> {
    val result = mutableListOf<SearchResult>()
    this.forEach {
        val payload = it.payloadMap
        result.add(
            SearchResult(
                id = it.id.num,
                score = it.score,
                payload = RecommendationPayload(
                    payload["stockCode"]?.stringValue ?: "_payload_missing_",
                    payload["result"]?.stringValue?.toFloat() ?: 0f,
                    payload["days"]?.stringValue?.toInt() ?: 0,
                    payload["closeAlertId"]?.stringValue ?: "_payload_missing_",
                    payload["closeAlertTweetId"]?.stringValue ?: "_payload_missing_",
                    payload["alertTweetId"]?.stringValue ?: "_payload_missing_"
                )
            )
        )
    }
    return result.toList()
}
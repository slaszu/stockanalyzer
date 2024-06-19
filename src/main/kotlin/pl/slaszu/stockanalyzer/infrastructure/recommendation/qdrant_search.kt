package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.ConditionFactory
import io.qdrant.client.QdrantClient
import io.qdrant.client.grpc.Points.*
import org.springframework.stereotype.Service
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
                    .setScoreThreshold(0.999f)
                    .setWithPayload(WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build()
            )
            .get();

        return searchResults.toSearchResultList()
    }

    override fun getVolumeScoreByAlert(stockVector: StockVector, alertTweetId: String): List<SearchResult> {

        val searchResults = client
            .searchAsync(
                SearchPoints.newBuilder()
                    .setCollectionName(config.collectionName)
                    .setVectorName("volume")
                    .addAllVector(stockVector.volumeVector.toMutableList())
                    .setLimit(20)
                    .setScoreThreshold(0.7f)
                    .setFilter(Filter.newBuilder().addMust(ConditionFactory.matchKeyword("alertTweetId", alertTweetId)))
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
                    payload["alertTweetId"]?.stringValue ?: "_payload_missing_"
                )
            )
        )
    }
    return result.toList()
}
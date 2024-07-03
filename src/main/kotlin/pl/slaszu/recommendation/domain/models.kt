package pl.slaszu.recommendation.domain

import pl.slaszu.shared_kernel.domain.alert.AlertModel


class StockVector(
    val priceVector: Array<Float>,
    val volumeVector: Array<Float>,
) {
    companion object {
        const val VECTOR_SIZE = 90
    }

    fun hasValidSize(): Boolean {
        return this.priceVector.size == VECTOR_SIZE && this.volumeVector.size == VECTOR_SIZE
    }
}


class RecommendationPayload(
    val stockCode: String?,
    private val alertAppId: String?,
    private val alertTweetId: String?
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "stockCode" to (this.stockCode ?: "stock_code_is_null"),
            "alertAppId" to (this.alertAppId ?: "alert_app_id_is_null"),
            "alertTweetId" to (this.alertTweetId ?: "alert_tweet_id_is_null")
        )
    }

    fun getId(): String? {
        return this.alertAppId ?: this.alertTweetId
    }

    companion object {
        fun fromAlert(alert: AlertModel): RecommendationPayload {
            return RecommendationPayload(
                alert.stockCode,
                alert.appId,
                alert.tweetId
            )
        }
    }
}

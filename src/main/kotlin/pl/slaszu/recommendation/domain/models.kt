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
    val stockCode: String,
    val alertTweetId: String
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "stockCode" to this.stockCode,
            "alertTweetId" to this.alertTweetId
        )
    }

    companion object {
        fun fromAlert(alert: AlertModel): RecommendationPayload {
            return RecommendationPayload(
                alert.stockCode,
                alert.tweetId
            )
        }
    }
}

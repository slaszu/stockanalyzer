package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel


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
    val result: Float,
    val days: Int,
    val closeAlertId: String,
    val closeAlertTweetId: String,
    val alertTweetId: String
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "stockCode" to this.stockCode,
            "result" to this.result.toString(),
            "days" to this.days.toString(),
            "closeAlertId" to this.closeAlertId,
            "closeAlertTweetId" to this.closeAlertTweetId,
            "alertTweetId" to this.alertTweetId
        )
    }

    companion object {
        fun fromCloseAlert(closeAlert: CloseAlertModel): RecommendationPayload {
            return RecommendationPayload(
                closeAlert.alert.stockCode,
                closeAlert.resultPercent,
                closeAlert.daysAfter,
                closeAlert.id!!,
                closeAlert.tweetId,
                closeAlert.alert.tweetId
            )
        }
    }
}

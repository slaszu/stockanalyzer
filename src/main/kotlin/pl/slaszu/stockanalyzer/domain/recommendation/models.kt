package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel

class RecommendationPayload(
    val stockCode: String,
    val result: Float,
    val days: Int,
    val closeAlertId: String,
    val tweetId: String
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "stockCode" to this.stockCode,
            "result" to this.result.toString(),
            "days" to this.days.toString(),
            "closeAlertId" to this.closeAlertId,
            "tweetId" to this.tweetId
        )
    }

    companion object {
        fun fromCloseAlert(closeAlert: CloseAlertModel): RecommendationPayload {
            return RecommendationPayload(
                closeAlert.alert.stockCode,
                closeAlert.resultPercent,
                closeAlert.daysAfter,
                closeAlert.id ?: "_undefined",
                closeAlert.tweetId
            )
        }
    }
}
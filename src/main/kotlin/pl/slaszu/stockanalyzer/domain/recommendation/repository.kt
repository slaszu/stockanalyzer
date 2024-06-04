package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel

interface SaveRepository {
    fun save(closeAlert: CloseAlertModel)
}

data class PersistPayload(
    val stockCode: String,
    val result: Float,
    val days: Int,
    val closeAlertId: String,
    val tweetId: String
)
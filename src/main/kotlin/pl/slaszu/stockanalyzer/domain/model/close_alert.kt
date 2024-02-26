package pl.slaszu.stockanalyzer.domain.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalEnum
import java.time.LocalDateTime

@Document("close_alert")
@TypeAlias("close_alert")
data class CloseAlertModel(
    val alert: AlertModel,
    val tweetId: String,
    val resultPercent: Float,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null
) {
}

interface CloseAlertRepository : MongoRepository<CloseAlertModel, String> {

}
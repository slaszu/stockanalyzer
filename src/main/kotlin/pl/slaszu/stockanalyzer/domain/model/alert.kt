package pl.slaszu.stockanalyzer.domain.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalEnum
import java.time.LocalDateTime

@Document("alert")
@TypeAlias("alert")
data class AlertModel(
    val stockCode: String,
    val stockName: String,
    val price: Float,
    val signals: List<SignalEnum>,
    val tweetId: String,
    val date: LocalDateTime = LocalDateTime.now(),
    var close: Boolean = false,
    val checkedProfit: Float? = null,
    val id: String? = null
) {
}

interface AlertRepository : MongoRepository<AlertModel, String> {
    fun findByDateAfterAndCloseIsFalse(date: LocalDateTime): List<AlertModel>

    fun findByDateBeforeAndCloseIsFalse(date: LocalDateTime): List<AlertModel>


}

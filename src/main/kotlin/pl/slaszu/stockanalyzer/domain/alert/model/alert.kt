package pl.slaszu.stockanalyzer.domain.alert.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
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
    val close: Boolean = false,
    val closeDate: LocalDateTime? = null,
    val id: String? = null
) {
}

interface AlertRepository : MongoRepository<AlertModel, String> {
    /**
     * test purpose only
     */
    fun findByDateAfterAndCloseIsFalse(date: LocalDateTime): List<AlertModel>

    @Query("{\$and: [{'date' : { \$lt: ?0 }}, {'close': false}]}")
    fun findAlertsActiveBeforeThatDate(date: LocalDateTime): List<AlertModel>

    @Query("{\$and: [{'closeDate' : { \$gt: ?0 }}, {'close': true}]}")
    fun findAlertsClosedAfterThatDate(date: LocalDateTime): List<AlertModel>
}

package pl.slaszu.stockanalyzer.domain.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDateTime

@Document("close_alert")
@TypeAlias("close_alert")
data class CloseAlertModel(
    val alert: AlertModel,
    val tweetId: String,
    val resultPercent: Float,
    val daysAfter: Int,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null
) {
}

interface CloseAlertRepository : MongoRepository<CloseAlertModel, String> {
    @Query("{\$and: [{'alert.stockCode': ?0}, {'daysAfter': ?1}, {'alert.close': false}]}")
    fun findByStockCodeAndDaysAfter(stockCode: String, daysAfter: Int): List<CloseAlertModel>

    @Query("{\$and: [{'daysAfter': ?0}, {'alert.close': ?1}]}")
    fun findByDaysAfterAndAlertClose(daysAfter: Int, alertClose:Boolean = false): List<CloseAlertModel>

    @Query("{ 'alert._id': ObjectId(?0) }")
    fun findByAlertId(alertId: String): List<CloseAlertModel>
}
package pl.slaszu.shared_kernel.domain.alert

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDateTime


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


interface CloseAlertRepository : MongoRepository<CloseAlertModel, String> {
    @Query("{\$and: [{'alert.stockCode': ?0}, {'daysAfter': ?1}, {'alert.close': false}]}")
    fun findByStockCodeAndDaysAfter(stockCode: String, daysAfter: Int): List<CloseAlertModel>

    @Query("{\$and: [{'daysAfter': ?0}, {'alert.close': ?1}]}")
    fun findByDaysAfterAndAlertClose(daysAfter: Int, alertClose: Boolean = false): List<CloseAlertModel>

    @Query("{ 'alert._id': ObjectId(?0) }")
    fun findByAlertId(alertId: String): List<CloseAlertModel>

    // todo add tests
    @Query("{'date' : { \$gt: ?0 }}")
    fun findCloseAlertsAfterDate(date: LocalDateTime): List<CloseAlertModel>

    // todo add tests
    @Query("{ 'alert.tweetId': ?0 }")
    fun findByAlertTweetId(alertTweetId: String): List<CloseAlertModel>
}

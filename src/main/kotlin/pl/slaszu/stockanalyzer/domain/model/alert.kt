package pl.slaszu.stockanalyzer.domain.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.slaszu.stockanalyzer.domain.stockanalyzer.SignalEnum
import java.time.LocalDateTime
import java.util.*

@Document("alert")
@TypeAlias("alert")
data class AlertModel(
    val stockCode: String,
    val price: Float,
    val signals: List<SignalEnum>,
    val date: LocalDateTime = LocalDateTime.now(),
    val checked: Boolean = false,
    val checkedProfit: Float? = null,
    val id: String? = null
) {
}

interface AlertRepository : MongoRepository<AlertModel, String> {
    fun findByStockCodeAndDateBefore(stock: String, date: Date): List<AlertModel>
}

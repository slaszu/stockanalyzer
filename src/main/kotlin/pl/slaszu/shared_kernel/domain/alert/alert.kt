package pl.slaszu.shared_kernel.domain.alert

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("alert")
@TypeAlias("alert")
data class AlertModel(
    val stockCode: String,
    val stockName: String,
    val price: Float,
    val signals: List<String>,
    val tweetId: String = "_undefined_",
    // dayAfter to predication result, eg. 14 to -3.456f
    val predictions: Map<Int, Float>? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val close: Boolean = false,
    val closeDate: LocalDateTime? = null,
    val id: String? = null,
)

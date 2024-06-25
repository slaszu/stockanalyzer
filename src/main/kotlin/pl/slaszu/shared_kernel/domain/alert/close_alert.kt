package pl.slaszu.shared_kernel.domain.alert

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("close_alert")
@TypeAlias("close_alert")
data class CloseAlertModel(
    val alert: AlertModel,
    val tweetId: String,
    val resultPercent: Float,
    val daysAfter: Int,
    val price: Float? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null
)

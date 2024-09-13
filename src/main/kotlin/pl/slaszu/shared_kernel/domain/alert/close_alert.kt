package pl.slaszu.shared_kernel.domain.alert

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import pl.slaszu.shared_kernel.domain.roundTo
import java.time.LocalDateTime

@Document("close_alert")
@TypeAlias("close_alert")
data class CloseAlertModel(
    val alert: AlertModel,
    val tweetId: String? = null,
    val resultPercent: Float,
    val daysAfter: Int,
    val price: Float? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null
) {
    fun getClosePrice(): Float? = this.price?.roundTo(2)

    fun getTitle(): String = "SELL ${alert.stockCode} ${this.getClosePrice()} PLN"
}

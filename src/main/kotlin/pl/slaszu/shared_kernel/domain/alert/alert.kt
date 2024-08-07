package pl.slaszu.shared_kernel.domain.alert

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import pl.slaszu.shared_kernel.domain.roundTo
import java.time.LocalDateTime

@Document("alert")
@TypeAlias("alert")
data class AlertModel(
    val stockCode: String,
    val stockName: String,
    val price: Float,
    val signals: List<String> = emptyList(),
    val tweetId: String? = null,
    val appId: String? = null,
    // dayAfter to predication result, eg. 14 to -3.456f
    val predictions: Map<Int, Float> = emptyMap(),
    val date: LocalDateTime = LocalDateTime.now(),
    val close: Boolean = false,
    val closeDate: LocalDateTime? = null,
    val blogLink: String? = null,
    val id: String? = null,
) {
    fun shouldBePublish(): Boolean {
        return !this.predictions.isNullOrEmpty()
    }

    fun getBuyPrice(): Float = this.price.roundTo(2)

    fun getTitle(): String = "BUY ${this.stockCode} ${this.getBuyPrice()} PLN"

    fun getPredicationText(): String {
        var predictionText = ""
        this.predictions.forEach { (dayAfter, result) ->
            predictionText += "${result.roundTo(2)}% (after $dayAfter days)\n"
        }
        if (predictionText.isNotBlank()) {
            predictionText = "similar signals (avg): \n$predictionText"
        }

        return predictionText
    }
}

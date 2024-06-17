package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.ValueFactory.value
import io.qdrant.client.grpc.JsonWithInt.Value
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.bio.viktor.F64Array
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.recommendation.StockVector
import pl.slaszu.stockanalyzer.domain.recommendation.StockVectorConverter
import pl.slaszu.stockanalyzer.domain.stock.StockProvider

@Service
class ViktorVectorConvert(private val stockProvider: StockProvider) : StockVectorConverter {
    override fun createVector(stockCode: String, dateTo: LocalDate): StockVector {
        val stockPriceList = this.stockProvider.getLastStockPriceList(
            stockCode,
            dateTo
        )

        val prices = mutableListOf<Float>()
        val amounts = mutableListOf<Int>()
        stockPriceList.takeLast(StockVector.VECTOR_SIZE).forEach {
            prices.add(it.price)
            amounts.add(it.amount)
        }

        val pricesF64 = F64Array(prices.size) { i ->
            prices[i].toDouble()
        }

        val amountF64 = F64Array(amounts.size) { i ->
            amounts[i].toDouble()
        }

        pricesF64.rescale()
        amountF64.rescale()

        return StockVector(
            pricesF64.data.map { it.toFloat() }.toTypedArray(),
            amountF64.data.map { it.toFloat() }.toTypedArray()
        )
    }

    override fun createVector(alert: AlertModel): StockVector {
        return this.createVector(alert.stockCode, alert.date.toLocalDate().toKotlinLocalDate())
    }
}

fun RecommendationPayload.toQdrantPayload(): Map<String, Value> {
    val result = mutableMapOf<String, Value>()
    this.toMap().forEach { (t, u) ->
        result[t] = value(u)
    }
    return result
}
package pl.slaszu.recommendation.infrastructure

import io.qdrant.client.ValueFactory.value
import io.qdrant.client.grpc.JsonWithInt.Value
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.bio.viktor.F64Array
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationPayload
import pl.slaszu.recommendation.domain.StockProvider
import pl.slaszu.recommendation.domain.StockVector
import pl.slaszu.recommendation.domain.StockVectorConverter
import pl.slaszu.shared_kernel.domain.alert.AlertModel

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
package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.ValueFactory.value
import io.qdrant.client.grpc.JsonWithInt.Value
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.bio.viktor.F64Array
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stock.StockProvider

enum class TensorDimensions(val size: Int) {
    SAMPLE_DIM(5),
    TENSOR_SIZE(60)
}

@Service
class VectorConvert(private val stockProvider: StockProvider) {

    fun create1DTensor(alert: AlertModel): Array<Float> {

        val stockPriceList = this.stockProvider.getLastStockPriceList(
            alert.stockCode,
            alert.date.toLocalDate().toKotlinLocalDate()
        )
        val create2DTensor = this.create2DTensor(stockPriceList)
        return create2DTensor.flatten().data.map { it.toFloat() }.toTypedArray()

    }

    fun create2DTensor(stockPriceList: Array<StockPriceDto>): F64Array {

        val vectors = mutableListOf<F64Array>()
        stockPriceList.takeLast(TensorDimensions.TENSOR_SIZE.size).forEach {
            vectors.add(it.toF64Array())
        }

        return F64Array(vectors.size, 5) { i, j ->
            vectors[i][j]
        }
    }
}

fun StockPriceDto.toF64Array(): F64Array {
    val array = arrayOf(
        this.price,
        this.priceLow,
        this.priceHigh,
        this.priceOpen,
        this.amount.toFloat()
    )

    val vector = F64Array(TensorDimensions.SAMPLE_DIM.size) { i ->
        array[i].toDouble()
    }

    vector.rescale()

    return vector
}

fun RecommendationPayload.toQdrantPayload(): Map<String, Value> {
    val result = mutableMapOf<String, Value>()
    this.toMap().forEach { t, u ->
        result[t] = value(u)
    }
    return result
}
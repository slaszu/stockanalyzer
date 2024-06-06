package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.ValueFactory.value
import io.qdrant.client.grpc.JsonWithInt.Value
import org.jetbrains.bio.viktor.F64Array
import org.jetbrains.bio.viktor.F64FlatArray
import org.jetbrains.bio.viktor.toF64Array
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stock.StockProvider


@Service
class VectorConvert(private val stockProvider: StockProvider) {

    fun create1DTensor(alert: AlertModel): Array<Float> {
        val stockPriceList = this.stockProvider.getStockPriceList(alert.stockCode)
        val create2DTensor = this.create2DTensor(stockPriceList)

        val toF64Array = F64Array(30, 5) { i, j ->
            create2DTensor[i][j].toDouble()
        }

        val copyLogInPLace = toF64Array.copy()
        copyLogInPLace.logInPlace()


        val copyRescale = copyLogInPLace.copy()
        copyRescale.rescale()



        val flatten = toF64Array.flatten()

        return emptyArray()
    }

    fun create2DTensor(stockPriceList: Array<StockPriceDto>, size: Int = 30): Array<Array<Float>> {
        val vector = mutableListOf<Array<Float>>()
        stockPriceList.take(size).forEach {
            vector.add(it.toArray())
        }
        return vector.toTypedArray()
    }
}

fun StockPriceDto.toArray(): Array<Float> {
    return arrayOf(
        this.price,
        this.priceLow,
        this.priceHigh,
        this.priceOpen,
        this.amount.toFloat()
    )
}

fun RecommendationPayload.toQdrantPayload(): Map<String, Value> {
    val result = mutableMapOf<String, Value>()
    this.toMap().forEach { t, u ->
        result[t] = value(u)
    }
    return result
}
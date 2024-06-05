package pl.slaszu.stockanalyzer.infrastructure.recommendation

import io.qdrant.client.ValueFactory.value
import io.qdrant.client.grpc.JsonWithInt.Value
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.RecommendationPayload


@Service
class VectorConvert {
    fun createVector(alert: AlertModel): Array<Double>  {
        return emptyArray()
    }
}

fun RecommendationPayload.toQdrantPayload(): Map<String, Value> {
    val result = mutableMapOf<String, Value>()
    this.toMap().forEach { t, u ->
        result[t] = value(u)
    }
    return result
}
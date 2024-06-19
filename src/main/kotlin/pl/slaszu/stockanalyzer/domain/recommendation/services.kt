package pl.slaszu.stockanalyzer.domain.recommendation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository

val logger = KotlinLogging.logger { }

interface StockVectorConverter {
    fun createVector(stockCode: String, dateTo: LocalDate): StockVector
    fun createVector(alert: AlertModel): StockVector
}

@Service
class RecommendationPersistService(
    private val saveRepo: SaveRepository,
    private val converter: StockVectorConverter
) {
    fun save(alert: AlertModel) {
        this.saveRepo.save(
            this.converter.createVector(alert),
            RecommendationPayload.fromAlert(alert)
        )
    }
}

@Service
class RecommendationSearchService(
    private val search: Search,
    private val converter: StockVectorConverter,
    private val closeAlertRepository: CloseAlertRepository
) {
    fun searchBestFit(stockCode: String, dateTo: LocalDate): List<BestFitResult> {
        val stockVector = this.converter.createVector(stockCode, dateTo)
        return this.searchBestFit(stockVector)
    }

    fun searchBestFit(alert: AlertModel): List<BestFitResult> {
        val stockVector = this.converter.createVector(alert)
        return this.searchBestFit(stockVector)
    }

    fun searchBestFit(stockVector: StockVector): List<BestFitResult> {

        val priceResult = this.search.searchByPrice(stockVector).associateBy { it.payload.alertTweetId }
//        priceResult.forEach {
//            logger.debug { "Price: $it" }
//        }

        val result = mutableMapOf<String, BestFitResult>()
        priceResult.forEach { (k, v) ->
            result.computeIfAbsent(k) {
                BestFitResult(
                    k,
                    v.score,
                    this.search.getVolumeScoreByAlert(stockVector, k).firstOrNull()?.score ?: 0f
                )
            }
        }

        return result.values.toList()
    }
}

data class BestFitResult(
    val alertTweetId: String,
    val priceScore: Float,
    val volumeScore: Float
)
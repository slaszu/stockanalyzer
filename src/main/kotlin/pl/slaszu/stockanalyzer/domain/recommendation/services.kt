package pl.slaszu.stockanalyzer.domain.recommendation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel

val logger = KotlinLogging.logger { }

interface StockVectorConverter {
    fun createVector(alert: AlertModel): StockVector
}

@Service
class RecommendationPersistService(
    private val saveRepo: SaveRepository,
    private val converter: StockVectorConverter
) {
    fun save(closeAlert: CloseAlertModel) {
        this.saveRepo.save(
            this.converter.createVector(closeAlert.alert),
            RecommendationPayload.fromCloseAlert(closeAlert)
        )
    }
}

@Service
class RecommendationSearchService(
    private val search: Search,
    private val converter: StockVectorConverter
) {
    fun searchBestFit(alert: AlertModel): List<BestFitResult> {
        val stockVector = this.converter.createVector(alert)
        val priceResult = this.search.searchByPrice(stockVector)
        val volumeResult = this.search.searchByVolume(stockVector)

        val result = mutableListOf<BestFitResult>()

        priceResult.forEach { price ->
            val volumeFound = volumeResult.find { volume ->
                volume.id == price.id
            }
            if (volumeFound != null) {
                result.add(
                    BestFitResult(
                        price,
                        price.score + volumeFound.score
                    )
                )
            }
        }

        // clear results
        val map = mutableMapOf<String, BestFitResult>()
        result.forEach { bestFit ->
            val identity = bestFit.searchResult.payload.alertTweetId
            map.computeIfAbsent(identity) {
                BestFitResult(bestFit.searchResult, bestFit.cumScore)
            }
            map.computeIfPresent(identity) { _, v ->
                if (v.searchResult.payload.result < bestFit.searchResult.payload.result) {
                    bestFit
                } else {
                    v
                }
            }
        }

        val clearResult = map.values.sortedByDescending { it.cumScore }
        clearResult.forEach {
            logger.debug { "Result BEST FIT: $it" }
        }

        return clearResult
    }
}

data class BestFitResult(
    val searchResult: SearchResult,
    val cumScore: Float
)
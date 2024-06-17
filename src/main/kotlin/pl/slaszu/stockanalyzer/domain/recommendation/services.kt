package pl.slaszu.stockanalyzer.domain.recommendation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
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

        val priceResult = this.search.searchByPrice(stockVector).associateBy { it.payload.alertTweetId }.keys
//        priceResult.forEach {
//            logger.debug { "Price: $it" }
//        }

        val volumeResult = this.search.searchByVolume(stockVector).associateBy { it.payload.alertTweetId }.keys
//        volumeResult.forEach {
//            logger.debug { "Volume: $it" }
//        }

        val final = priceResult.intersect(volumeResult)
        val closeAlertList = mutableListOf<CloseAlertModel>()
        final.forEach {
            //logger.debug { "Final: $it" }
            closeAlertList.addAll(this.closeAlertRepository.findByAlertTweetId(it))
        }

        val result = mutableMapOf<Int, Double>()
        closeAlertList.groupBy { it.daysAfter }.forEach { t, u ->
            result[t] = u.sumOf { it.resultPercent.toDouble() } / u.size
        }

        logger.debug { "Result $result" }

        /*
        1. get prices similar chart
        1a. get only alertTweetId list/set
        2. get volume similar chart
        2a. get only alertTweetId list/set
        3. get intersect of alertTweetId from price and volumes
         */


//        val result = mutableListOf<BestFitResult>()
//
//        priceResult.forEach { price ->
//            val volumeFound = volumeResult.find { volume ->
//                volume.id == price.id
//            }
//            if (volumeFound != null) {
//                result.add(
//                    BestFitResult(
//                        price,
//                        price.score + volumeFound.score
//                    )
//                )
//            }
//        }

//        // clear results
//        val map = mutableMapOf<String, BestFitResult>()
//        result.forEach { bestFit ->
//            val identity = bestFit.searchResult.payload.alertTweetId
//            map.computeIfAbsent(identity) {
//                BestFitResult(bestFit.searchResult, bestFit.cumScore)
//            }
//            map.computeIfPresent(identity) { _, v ->
//                if (v.searchResult.payload.result < bestFit.searchResult.payload.result) {
//                    bestFit
//                } else {
//                    v
//                }
//            }
//        }
//
//        val clearResult = map.values.sortedByDescending { it.cumScore }
//        clearResult.forEach {
//            logger.debug { "Result BEST FIT: $it" }
//        }

        return emptyList()

    }
}

data class BestFitResult(
    val searchResult: SearchResult,
    val cumScore: Float
)
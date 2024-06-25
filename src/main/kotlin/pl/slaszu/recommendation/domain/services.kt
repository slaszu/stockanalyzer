package pl.slaszu.recommendation.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository

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

data class BestFitResult(
    val alertTweetId: String,
    val priceScore: Float,
    val volumeScore: Float
)

@Service
class SimilarAlertSearchService(
    private val search: Search,
    private val converter: StockVectorConverter
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
                    this.search.getVolumeScoreForAlert(stockVector, k).firstOrNull()?.score ?: 0f
                )
            }
        }

        return result.values.toList()
    }
}

class Recommendation() {
    private val dayToCloseAlertList = mutableMapOf<BestFitResult, List<CloseAlertModel>>()

    fun add(bestFit: BestFitResult, closeAlertList: List<CloseAlertModel>) {
        this.dayToCloseAlertList[bestFit] = closeAlertList
    }

    fun getDaysAfterToResultAvg(): Map<Int, Float> {
        val tmpRes = mutableMapOf<Int,MutableList<Float>>()
        this.dayToCloseAlertList.forEach { (t, u) ->
            u.forEach { closeAlert ->
                if (tmpRes[closeAlert.daysAfter] == null) {
                    tmpRes[closeAlert.daysAfter] = mutableListOf(closeAlert.resultPercent)
                } else {
                    tmpRes[closeAlert.daysAfter]!!.add(closeAlert.resultPercent)
                }
            }
        }

        val res = mutableMapOf<Int, Float>()
        tmpRes.forEach { (t, u) ->
            res[t] = u.average().toFloat()
        }

        return res
    }
}

@Service
class RecommendationService(
    private val closeAlertRepository: CloseAlertRepository
) {
    fun convertToRecommendation(bestFitResults: List<BestFitResult>): Recommendation {
        val reco = Recommendation()
        bestFitResults.forEach {
            reco.add(
                it,
                this.closeAlertRepository.findByAlertTweetId(it.alertTweetId)
            )
        }

        return reco
    }
}

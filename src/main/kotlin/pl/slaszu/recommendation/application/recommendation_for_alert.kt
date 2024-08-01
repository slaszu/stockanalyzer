package pl.slaszu.recommendation.application

import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationService
import pl.slaszu.recommendation.domain.SimilarAlertSearchService
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel

@Service
class RecommendationForAlert(
    private val recommendationService: RecommendationService,
    private val searchService: SimilarAlertSearchService
) {
    fun getPredictionsMap(alert: AlertModel): Map<Int,Float> {
        val searchBestFitList = this.searchService.searchBestFit(alert)
        if (searchBestFitList.isEmpty()) {
            return emptyMap()
        }

        val reco = this.recommendationService.convertToRecommendation(searchBestFitList)
        return reco.getDaysAfterToResultAvg()
    }

    fun getCloseAlertModelListOfList(alert: AlertModel): List<List<CloseAlertModel>> {
        val searchBestFitList = this.searchService.searchBestFit(alert)
        if (searchBestFitList.isEmpty()) {
            return emptyList()
        }

        val reco = this.recommendationService.convertToRecommendation(searchBestFitList)
        return reco.getCloseAlertListOfList()
    }
}
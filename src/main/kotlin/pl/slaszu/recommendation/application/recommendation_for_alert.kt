package pl.slaszu.recommendation.application

import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationService
import pl.slaszu.recommendation.domain.SimilarAlertSearchService
import pl.slaszu.shared_kernel.domain.alert.AlertModel

@Service
class RecommendationForAlert(
    private val recommendationService: RecommendationService,
    private val searchService: SimilarAlertSearchService
) {
    fun addRecommendationIfExists(alert: AlertModel): AlertModel {
        val searchBestFitList = this.searchService.searchBestFit(alert)
        if (searchBestFitList.isEmpty()) {
            return alert;
        }

        val reco = this.recommendationService.convertToRecommendation(searchBestFitList)
        return alert.copy(predictions = reco.getDaysAfterToResultAvg())
    }
}
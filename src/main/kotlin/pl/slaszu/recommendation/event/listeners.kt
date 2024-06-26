package pl.slaszu.recommendation.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationService
import pl.slaszu.recommendation.domain.SimilarAlertSearchService
import pl.slaszu.stockanalyzer.domain.event.CreateAlertEvent

@Service
class CreateAlertEventListener(
    private val recommendationService: RecommendationService,
    private val searchService: SimilarAlertSearchService
) {

    @EventListener
    fun addRecommendationToAlert(event: CreateAlertEvent) {
        val alert = event.createdAlert

        val searchBestFitList = this.searchService.searchBestFit(alert)
        if (searchBestFitList.isEmpty()) {
            return;
        }

        val reco = this.recommendationService.convertToRecommendation(searchBestFitList)
        val newAlert = alert.copy(predictions = reco.getDaysAfterToResultAvg())

        event.changedAlert = newAlert
    }
}
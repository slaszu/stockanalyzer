package pl.slaszu.recommendation.event

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.*
import pl.slaszu.stockanalyzer.domain.event.CreateAlertEvent
import pl.slaszu.stockanalyzer.domain.event.PersistAlertEvent

@Service
class StockanalyzerEventListener(
    private val recommendationService: RecommendationService,
    private val searchService: SimilarAlertSearchService,
    private val recommendationRepository: RecommendationRepository,
    private val vectorConverter: StockVectorConverter
) {
    private val logger = KotlinLogging.logger { }

    // todo test for listeners
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

    // todo test for listeners
    @EventListener
    @Async
    fun sendAlertToRecommendationSystem(event: PersistAlertEvent) {
        this.recommendationRepository.save(
            this.vectorConverter.createVector(event.alert),
            RecommendationPayload.fromAlert(event.alert)
        )
        this.logger.debug { "Alert added to recommendation db: ${event.alert}" }
    }
}
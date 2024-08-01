package pl.slaszu.recommendation.event

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationPayload
import pl.slaszu.recommendation.domain.RecommendationRepository
import pl.slaszu.recommendation.domain.StockVectorConverter
import pl.slaszu.stockanalyzer.domain.event.PersistAlertAfterEvent

@Service
class RecommendationEventListener(
    private val recommendationRepository: RecommendationRepository,
    private val vectorConverter: StockVectorConverter
) {
    private val logger = KotlinLogging.logger { }

    @EventListener
    @Async
    fun sendAlertToRecommendationSystem(event: PersistAlertAfterEvent) {
        this.recommendationRepository.save(
            this.vectorConverter.createVector(event.alert),
            RecommendationPayload.fromAlert(event.alert)
        )
        this.logger.debug { "Alert added to recommendation db: ${event.alert}" }
    }
}
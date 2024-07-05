package pl.slaszu.recommendation.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertRepository

@Service
class RecommendationRepositoryFillService(
    private val alertRepository: AlertRepository,
    private val recommendationPersistService: RecommendationPersistService,
    private val repository: RecommendationRepository
) {
    private val logger = KotlinLogging.logger { }

    fun createAndFillRepositoryIfNotExists(): Boolean {
        if (!this.repository.createIfNotExists()) {
            logger.debug { "Recommendation db initialized" }
            this.alertRepository.findAll()
                .also {
                    logger.debug { "${it.size} alerts will be added to recommendation db ..." }
                }
                .forEach {
                    this.recommendationPersistService.save(it)
                }
            logger.debug { "Done !" }
            return true
        } else {
            logger.debug { "Recommendation db already exists" }
            return false
        }
    }
}
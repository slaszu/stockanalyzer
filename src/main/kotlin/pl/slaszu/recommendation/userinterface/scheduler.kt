package pl.slaszu.recommendation.userinterface

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationPersistService
import pl.slaszu.recommendation.domain.RecommendationRepository
import pl.slaszu.shared_kernel.domain.alert.AlertRepository

@Service
//@Profile("prod")
class SchedulerRecommendation(
    val recommendationRepository: RecommendationRepository,
    val recommendationPersistService: RecommendationPersistService,
    val alertRepository: AlertRepository
) {

    private val logger = KotlinLogging.logger { }

    @Scheduled(cron = "0 * * * * *")
    @Profile("default")
    fun runTest() {
        logger.info { "SchedulerRecommendation:runTest do nothing" }
    }

    @Scheduled(cron = "0 * * * * *")
    fun runCreateRecommendationDb() {
        logger.info { "SchedulerRecommendation:runCreateRecommendationDb" }
        if (!this.recommendationRepository.createIfNotExists()) {
            logger.debug { "Recommendation db initialized" }
            this.alertRepository.findAll()
                .also {
                    logger.debug { "${it.size} alerts will be added to recommendation db ..." }
                }
                .forEach {
                    this.recommendationPersistService.save(it)
                }
            logger.debug { "Done !" }
        } else {
            logger.debug { "Recommendation db already exists" }
        }
    }
}

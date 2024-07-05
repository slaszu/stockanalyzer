package pl.slaszu.recommendation.userinterface

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.slaszu.recommendation.domain.RecommendationRepositoryFillService

@Service
@Profile("prod")
class SchedulerRecommendation(
    val recommendationRepositoryFillService: RecommendationRepositoryFillService
) {

    private val logger = KotlinLogging.logger { }

    @Scheduled(cron = "0 * * * * *")
    @Profile("default")
    fun runTest() {
        logger.info { "SchedulerRecommendation:runTest do nothing" }
    }

    @Scheduled(cron = "0 * * * * *")
    fun runCreateRecommendationDb() {
        this.recommendationRepositoryFillService.createAndFillRepositoryIfNotExists()
    }
}

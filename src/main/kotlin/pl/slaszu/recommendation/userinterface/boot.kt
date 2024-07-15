package pl.slaszu.recommendation.userinterface

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import pl.slaszu.recommendation.domain.RecommendationRepositoryFillService

@Configuration
@Profile("!test")
class RecommendationBoot(
    private val recommendationRepositoryFillService: RecommendationRepositoryFillService
) {
    private val logger = KotlinLogging.logger { }

    @Bean
    fun createCollectionIfNotExists(): ApplicationRunner =
        ApplicationRunner {
            //this.recommendationRepositoryFillService.createAndFillRepositoryIfNotExists()
        }
}
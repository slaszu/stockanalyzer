package pl.slaszu.recommendation.userinterface

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import pl.slaszu.recommendation.domain.RecommendationRepository

@Configuration
@Profile("!test")
class RecommendationBoot(
    private val recommendationRepository: RecommendationRepository
) {
    @Bean
    fun createCollectionIfNotExists(): ApplicationRunner =
        ApplicationRunner { recommendationRepository.createIfNotExists() }
}
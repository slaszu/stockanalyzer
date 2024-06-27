package pl.slaszu.recommendation.domain

interface RecommendationRepository {
    fun save(vector: StockVector, payload: RecommendationPayload)

    fun createIfNotExists(): Boolean
}
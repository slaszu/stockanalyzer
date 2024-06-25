package pl.slaszu.recommendation.domain

interface SaveRepository {
    fun save(vector: StockVector, payload: RecommendationPayload)
}

package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel

interface SaveRepository {
    fun save(vector: StockVector, payload: RecommendationPayload)
}

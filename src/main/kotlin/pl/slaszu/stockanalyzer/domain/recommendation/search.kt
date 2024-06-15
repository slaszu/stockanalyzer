package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel

interface Search {
    fun searchByPrice(stockVector: StockVector): List<SearchResult>
    fun searchByVolume(stockVector: StockVector): List<SearchResult>
}

data class SearchResult(
    val id: Long,
    val score: Float,
    val payload: RecommendationPayload
) {
    override fun toString(): String {
        return "SearchResult(id=$id, score=$score, payload=${payload.toMap()})"
    }
}
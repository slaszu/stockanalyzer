package pl.slaszu.stockanalyzer.domain.recommendation

import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel

interface Search {
    fun search(alertModel: AlertModel): List<SearchResult>
}

data class SearchResult(
    val score: Float,
    val payload: RecommendationPayload,

) {
    override fun toString(): String {
        return "SearchResult(score=$score, payload=${payload.toMap()})"
    }
}
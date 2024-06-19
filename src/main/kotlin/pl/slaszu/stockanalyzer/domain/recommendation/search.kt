package pl.slaszu.stockanalyzer.domain.recommendation

interface Search {
    fun searchByPrice(stockVector: StockVector): List<SearchResult>
    fun getVolumeScoreForAlert(stockVector: StockVector, alertTweetId: String): List<SearchResult>
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
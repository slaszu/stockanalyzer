package pl.slaszu.unit.recommendation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pl.slaszu.recommendation.domain.RecommendationPayload
import pl.slaszu.shared_kernel.domain.alert.AlertModel

class RecommendationPayloadTest {

    @Test
    fun `recommendation payload checks`() {

        val alert = AlertModel("TST", "Test", 5f, tweetId = null, appId = "1234")

        val payload = RecommendationPayload.fromAlert(alert)

        Assertions.assertEquals("1234", payload.getId())
        Assertions.assertEquals(
            mapOf("stockCode" to "TST", "alertAppId" to "1234", "alertTweetId" to null),
            payload.toMap()
        )
    }
}
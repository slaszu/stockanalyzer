package pl.slaszu.unit.shared

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pl.slaszu.shared_kernel.domain.alert.AlertModel

class AlertTest {

    @Test
    fun `alert should be published`() {

        val alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
        )

        Assertions.assertFalse(alert.shouldBePublish())

        val alertWithTweetId = alert.copy(tweetId = "12343232")

        Assertions.assertTrue(alertWithTweetId.shouldBePublish())

        val alertWithPredication = alert.copy(predictions = mapOf(7 to 1.5f, 14 to -2.3f))

        Assertions.assertTrue(alertWithPredication.shouldBePublish())

        val alertWithTweetAndPredication = alert.copy(tweetId = "1212121", predictions = mapOf(7 to 3.4f))

        Assertions.assertTrue(alertWithTweetAndPredication.shouldBePublish())
    }

}

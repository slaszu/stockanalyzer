package pl.slaszu.unit.shared

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pl.slaszu.shared_kernel.domain.alert.AlertModel

class AlertTest {

    @Test
    fun `not publish if there is not tweet id and no predications`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
        )

        Assertions.assertFalse(alert.shouldBePublish())
    }

    @Test
    fun `not publish if there is empty tweet id and no predications`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            tweetId = ""
        )

        Assertions.assertFalse(alert.shouldBePublish())
    }

    @Test
    fun `publish if there is tweet id`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            tweetId = "123123"
        )

        Assertions.assertTrue(alert.shouldBePublish())
    }

    @Test
    fun `no publish if predications are lower then threashold`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            predictions = mapOf(7 to 2f, 14 to 3f)
        )

        Assertions.assertFalse(alert.shouldBePublish())
    }

    @Test
    fun `no publish if last predication is lower then threashold`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            predictions = mapOf(7 to 5f, 14 to 4.9f)
        )

        Assertions.assertFalse(alert.shouldBePublish())
    }

    @Test
    fun `publish if last predication is equal then threashold`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            predictions = mapOf(7 to 4.9f, 14 to 5f)
        )

        Assertions.assertTrue(alert.shouldBePublish())
    }

    @Test
    fun `publish if last predication is greater then threashold`() {

        var alert = AlertModel(
            stockCode = "CODE",
            stockName = "Name",
            price = 120f,
            predictions = mapOf(7 to 4.9f, 14 to 5.1f)
        )

        Assertions.assertTrue(alert.shouldBePublish())
    }

}

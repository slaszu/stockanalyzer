package pl.slaszu.integration.stockanalyzer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.slaszu.recommendation.event.StockanalyzerEventListener
import pl.slaszu.shared_kernel.domain.stock.StockDto
import pl.slaszu.stockanalyzer.domain.alert.AlertService


@SpringBootTest
@ActiveProfiles("test")

class AlertServiceTests(
    @Autowired val alertService: AlertService
) {

    @MockkBean
    private lateinit var stockanalyzerEventListener: StockanalyzerEventListener

    @Test
    fun testCreateAlert(
    ) {
        val stockDto = StockDto("Test", "TST")

        every { stockanalyzerEventListener.addRecommendationToAlert(any()) } returns Unit

        this.alertService.createAlert(
            stockDto,
            3.5f,
            emptyList()
        )

        verify { stockanalyzerEventListener.addRecommendationToAlert(any()) }
    }
}
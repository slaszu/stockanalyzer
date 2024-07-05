package pl.slaszu.integration.stockanalyzer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.slaszu.recommendation.event.RecommendationEventListener
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.stock.StockDto
import pl.slaszu.stockanalyzer.domain.alert.AlertService
import pl.slaszu.stockanalyzer.domain.event.CreateAlertEvent
import pl.slaszu.stockanalyzer.domain.event.PersistAlertAfterEvent


@SpringBootTest
@ActiveProfiles("test")
class AlertServiceTests() {

    @Autowired
    private lateinit var alertService: AlertService

    @MockkBean
    private lateinit var recoEventListener: RecommendationEventListener

    @MockkBean
    private lateinit var alertRepository: AlertRepository

    @Test
    fun testCreateAlert() {
        val stockDto = StockDto("Test", "TST")

        fun matcher(event: CreateAlertEvent): Boolean {
            return event.createdAlert.stockCode.equals("TST") &&
                    event.createdAlert.price.equals(3.5f)
        }

        every {
            recoEventListener.addRecommendationToAlert(match { matcher(it) })
        } returns Unit

        this.alertService.createAlert(
            stockDto,
            3.5f,
            emptyList()
        )

        verify { recoEventListener.addRecommendationToAlert(any()) }
    }

    @Test
    fun testPersistAlert() {

        val alert = AlertModel(
            "TST",
            "Test",
            3.5f,
            emptyList()
        )

        fun matcherRepo(alertToTest: AlertModel): Boolean {
            return alertToTest == alert
        }

        fun matcherListener(event: PersistAlertAfterEvent): Boolean {
            return event.alert.id == "123"
        }

        every { alertRepository.save(match { matcherRepo(it) }) } returns alert.copy(id = "123")

        every { recoEventListener.sendAlertToRecommendationSystem(match { matcherListener(it) }) } returns Unit


        this.alertService.persistAlert(alert)

        verify { recoEventListener.sendAlertToRecommendationSystem(any()) }
        verify { alertRepository.save(any()) }

    }
}
package pl.slaszu.integration

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import pl.slaszu.integration.config.MongoDBTestContainerConfig
import pl.slaszu.StockanalyzerApplication
import pl.slaszu.stockanalyzer.domain.alert.CloseAlertService
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import java.time.LocalDateTime


@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@SpringBootTest
@ActiveProfiles("test")
class CloseAlertServiceTests(
    @Autowired val alertRepo: AlertRepository,
    @Autowired val closeAlertRepo: CloseAlertRepository,
    @Autowired val closeAlertService: CloseAlertService
) {

    private var alertSaved: AlertModel? = null

    @AfterEach
    fun del_fixtures() {
        this.closeAlertRepo.deleteAll()
        this.alertRepo.deleteAll()
    }


    @BeforeEach
    fun insert_fixtures() {

        val alert = AlertModel("PLW", "PLAYWAY", 286.45f, emptyList(), "fakeTweetId")

        this.alertSaved = this.alertRepo.save(alert)

        this.closeAlertRepo.save(
            CloseAlertModel(
                this.alertSaved!!,
                "tweetIdx33",
                5f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                this.alertSaved!!,
                "tweetIdx22",
                -3f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                this.alertSaved!!,
                "tweetIdx11",
                -3f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                this.alertSaved!!,
                "tweetIdx11",
                -3f,
                3
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "Y", "name y", 3.5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true
                ),
                "tweetIdx11",
                -3f,
                2
            )
        )

    }

    @Test
    fun testGetAll() {
        val findAll = closeAlertRepo.findAll()
        Assertions.assertEquals(5, findAll.size)

        val findByDaysAfterAndAlertClose = closeAlertRepo.findByDaysAfterAndAlertClose(2, false)
        Assertions.assertEquals(3, findByDaysAfterAndAlertClose.size)

        val findByAlertId = closeAlertRepo.findByAlertId(this.alertSaved?.id!!)
        Assertions.assertEquals(4, findByAlertId.size)

        val findByCloseDateAfterAndCloseIsFalse =
            alertRepo.findAlertsClosedAfterThatDate(LocalDateTime.now().minusDays(1))
        Assertions.assertEquals(0, findByCloseDateAfterAndCloseIsFalse.size)

        this.closeAlertService.closeAlert(this.alertSaved!!)

        val findByAlertIdAfter = closeAlertRepo.findByAlertId(this.alertSaved?.id!!)
        Assertions.assertEquals(4, findByAlertIdAfter.size)
        findByAlertIdAfter.forEach {
            Assumptions.assumeTrue(it.alert.close)
        }

        val findById = alertRepo.findById(this.alertSaved?.id!!)
        Assertions.assertTrue(findById.get().close)

        val findByCloseDateAfterAndCloseIsFalseAfter =
            alertRepo.findAlertsClosedAfterThatDate(LocalDateTime.now().minusDays(1))
        Assertions.assertEquals(1, findByCloseDateAfterAndCloseIsFalseAfter.size)

        val findByCloseDateAfterAndCloseIsFalseAfterNow =
            alertRepo.findAlertsClosedAfterThatDate(LocalDateTime.now())
        Assertions.assertEquals(0, findByCloseDateAfterAndCloseIsFalseAfterNow.size)
    }

}
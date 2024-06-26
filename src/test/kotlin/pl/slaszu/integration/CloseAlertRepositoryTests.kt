package pl.slaszu.integration

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import pl.slaszu.integration.config.MongoDBTestContainerConfig
import pl.slaszu.StockanalyzerApplication
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import java.time.LocalDateTime
import java.util.stream.Stream


@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class CloseAlertRepositoryTests(
    @Autowired val closeAlertRepo: CloseAlertRepository
) {

    @AfterEach
    fun del_fixtures() {
        this.closeAlertRepo.deleteAll()
    }

    fun getAlertId(): String {
        return "65eb0befc864193a40a3d007"
    }

    @BeforeEach
    fun insert_fixtures() {


        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "X", "name", 5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true, null, getAlertId()
                ),
                "tweetIdx33",
                5f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "Y", "name y", 3.5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true, null, getAlertId()
                ),
                "tweetIdx22",
                -3f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "Y", "name y", 3.5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true, null, getAlertId()
                ),
                "tweetIdx11",
                -3f,
                2
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "X", "name", 5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true
                ),
                "tweetIdx",
                5f,
                7
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "Y", "name y", 3.5f, emptyList(), "tweetId", null,
                    LocalDateTime.now(), true
                ),
                "tweetIdx",
                -3f,
                7
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "X", "name", 5f, emptyList(), "tweetId"
                ),
                "tweetIdx",
                5f,
                7
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "Y", "name y", 3.5f, emptyList(), "tweetId"
                ),
                "tweetIdx",
                -3f,
                7
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "X", "name", 4f, emptyList(), "tweetId"
                ),
                "tweetIdx",
                2.5f,
                7
            )
        )

        this.closeAlertRepo.save(
            CloseAlertModel(
                AlertModel(
                    "X", "name", 4.8f, emptyList(), "tweetId"
                ),
                "tweetIdx",
                1f,
                14
            )
        )
    }

    @Test
    fun testGetAll() {
        val findAll = closeAlertRepo.findAll()
        Assertions.assertEquals(9, findAll.size)
    }


    @ParameterizedTest
    @MethodSource("getData")
    fun testData(stockCode: String, daysAfter: Int, expect: Int) {

        // check all
        val closeAlertModels = closeAlertRepo.findByStockCodeAndDaysAfter(stockCode, daysAfter)
        Assertions.assertEquals(expect, closeAlertModels.size)
    }

    @Test
    fun testDaysAfterAndAlertCloseIsFalse() {
        val findByDaysAfter = closeAlertRepo.findByDaysAfterAndAlertClose(7)
        Assertions.assertEquals(3, findByDaysAfter.size)
    }

    @Test
    fun testDaysAfterAndAlertCloseIsTrue() {
        val findByDaysAfter = closeAlertRepo.findByDaysAfterAndAlertClose(7, true)
        Assertions.assertEquals(2, findByDaysAfter.size)
    }

    @Test
    fun testFindByAlertId() {
        val findByDaysAfter = closeAlertRepo.findByAlertId(getAlertId())
        Assertions.assertEquals(3, findByDaysAfter.size)
    }



    companion object {
        @JvmStatic
        fun getData(): Stream<Arguments> {
            return listOf(
                Arguments.of(
                    "X", 7, 2
                ),
                Arguments.of(
                    "Y", 7, 1
                ),
                Arguments.of(
                    "X", 23, 0
                ),
            ).stream()
        }
    }

}
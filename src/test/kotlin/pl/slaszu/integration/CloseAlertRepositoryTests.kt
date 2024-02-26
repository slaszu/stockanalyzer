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
import pl.slaszu.stockanalyzer.StockanalyzerApplication
import pl.slaszu.stockanalyzer.domain.model.AlertModel
import pl.slaszu.stockanalyzer.domain.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.model.CloseAlertRepository
import java.util.stream.Stream


@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class CloseAlertRepositoryTests(@Autowired val closeAlertRepo: CloseAlertRepository) {

    @AfterEach
    fun del_fixtures() {
        this.closeAlertRepo.deleteAll()
    }

    @BeforeEach
    fun insert_fixtures() {
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
        Assertions.assertEquals(4, findAll.size)
    }


    @ParameterizedTest
    @MethodSource("getData")
    fun testData(stockCode: String, daysAfter: Int, expect: Int) {

        // check all
        var closeAlertModels = closeAlertRepo.findByStockCodeAndDaysAfter(stockCode, daysAfter)
        Assertions.assertEquals(expect, closeAlertModels.size)
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
package pl.slaszu.integration

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
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
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import java.util.stream.Stream
import java.time.LocalDateTime as LocalDateTimeJava

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class AlertRepositoryTests(@Autowired val alertRepo: AlertRepository) {

    @AfterEach
    fun del_fixtures() {
        this.alertRepo.deleteAll()
    }

    @BeforeEach
    fun insert_fixtures() {
        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(), "",
                LocalDateTime.parse("2023-01-01T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.0f, emptyList(), "",
                LocalDateTime.parse("2023-01-02T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.1f, emptyList(), "",
                LocalDateTime.parse("2023-01-03T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.2f, emptyList(), "",
                LocalDateTime.parse("2023-01-03T13:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(), "",
                LocalDateTime.parse("2023-01-03T14:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(), "",
                LocalDateTime.parse("2023-01-06T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(), "",
                LocalDateTime.parse("2023-01-07T12:00:00").toJavaLocalDateTime()
            )
        )
    }

    @Test
    fun testGetAll() {
        val findAll = alertRepo.findAll()
        Assertions.assertEquals(7, findAll.size)
    }


    @ParameterizedTest
    @MethodSource("getDatesAfter")
    fun testDatesAfter(date: LocalDateTimeJava, expect: Int) {

        // check all
        var alertModels = alertRepo.findByDateAfterAndCloseIsFalse(date)
        Assertions.assertEquals(expect, alertModels.size)
    }

    @ParameterizedTest
    @MethodSource("getDatesBefore")
    fun testDatesBefore(date: LocalDateTimeJava, expect: Int) {

        // check all
        var alertModels = alertRepo.findByDateBeforeAndCloseIsFalse(date)
        Assertions.assertEquals(expect, alertModels.size)
    }

    @Test
    fun testCloseAll() {
        val date = LocalDateTimeJava.of(2023, 1, 3, 11, 59, 0)

        var alertModels = alertRepo.findByDateAfterAndCloseIsFalse(date)

        // close all found
        alertModels.forEach {
            val copy = it.copy(close = true)
            alertRepo.save(copy)
        }

        // get again not close after date
        val alertModelsAfter = alertRepo.findByDateAfterAndCloseIsFalse(date)
        Assertions.assertEquals(0, alertModelsAfter.size)

    }

    companion object {
        @JvmStatic
        fun getDatesBefore(): Stream<Arguments> {
            return listOf(
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 11, 59, 0),
                    2
                ),
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 12, 0, 0),
                    2
                ),
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 12, 59, 0),
                    3
                )
            ).stream()
        }

        @JvmStatic
        fun getDatesAfter(): Stream<Arguments> {
            return listOf(
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 11, 59, 0),
                    5
                ),
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 12, 0, 0),
                    4
                ),
                Arguments.of(
                    LocalDateTimeJava.of(2023, 1, 3, 12, 59, 0),
                    4
                )
            ).stream()
        }
    }

}
package pl.slaszu.integration

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import pl.slaszu.integration.config.MongoDBTestContainerConfig
import pl.slaszu.stockanalyzer.StockanalyzerApplication
import pl.slaszu.stockanalyzer.domain.model.AlertModel
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import pl.slaszu.stockanalyzer.shared.toDate
import java.time.LocalDate

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class AlertRepositoryTests(@Autowired val alertRepo: AlertRepository) {

    @BeforeEach
    fun insert_fixtures() {
        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(),
                LocalDateTime.parse("2023-01-01T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.0f, emptyList(),
                LocalDateTime.parse("2023-01-02T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.1f, emptyList(),
                LocalDateTime.parse("2023-01-03T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.2f, emptyList(),
                LocalDateTime.parse("2023-01-04T12:00:00").toJavaLocalDateTime()
            )
        )

        this.alertRepo.save(
            AlertModel(
                "XYZ", 5.3f, emptyList(),
                LocalDateTime.parse("2023-01-05T12:00:00").toJavaLocalDateTime()
            )
        )
    }

    @Test
    fun test() {

        // check all
        val findAll = alertRepo.findAll()
        Assertions.assertEquals(5, findAll.size)

        val date = LocalDate.of(2023, 1, 3).toDate()

        // get not close after date
        val alertModels = alertRepo.findByDateAfterAndCloseIsFalse(date)
        Assertions.assertEquals(3, alertModels.size)

        // close all found
        alertModels.forEach {
            val copy = it.copy(close = true)
            alertRepo.save(copy)
        }

        // get again not close after date
        val alertModelsAfter = alertRepo.findByDateAfterAndCloseIsFalse(date)
        Assertions.assertEquals(0, alertModelsAfter.size)


    }
}
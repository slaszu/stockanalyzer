package pl.slaszu.integration

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
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

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class AlertRepositoryTests(@Autowired val alertRepo: AlertRepository) {

    @BeforeEach
    fun insert_fixtures() {
        this.alertRepo.save(
            AlertModel("XYZ", 5.6f, emptyList(),
                LocalDateTime(2023, 1, 10, 12, 0, 0, 0).toJavaLocalDateTime())
        )

        this.alertRepo.save(
            AlertModel("XYZ", 5.3f, emptyList(),
                LocalDateTime.parse("2023-01-01T12:00:00").toJavaLocalDateTime())
        )
    }

    @Test
    fun test() {

        val findAll = alertRepo.findAll()
        Assertions.assertEquals(2, findAll.size)

    }
}
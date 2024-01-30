package pl.slaszu.integration

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

    @Test
    fun test() {

        alertRepo.save(AlertModel("XYZ", 5.7f, emptyList()))

    }
}
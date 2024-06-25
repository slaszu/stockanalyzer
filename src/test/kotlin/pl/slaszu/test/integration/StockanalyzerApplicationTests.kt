package pl.slaszu.test.integration

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import pl.slaszu.test.integration.config.MongoDBTestContainerConfig
import pl.slaszu.StockanalyzerApplication

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class, StockanalyzerApplication::class])
@ActiveProfiles("test")
class StockanalyzerApplicationTests {

	@Test
	fun contextLoads() {
	}

}

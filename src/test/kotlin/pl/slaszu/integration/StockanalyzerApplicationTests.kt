package pl.slaszu.integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.slaszu.stockanalyzer.StockanalyzerApplication

@SpringBootTest(classes = [StockanalyzerApplication::class])
@ActiveProfiles("test")
class StockanalyzerApplicationTests {

	@Test
	fun contextLoads() {
	}

}

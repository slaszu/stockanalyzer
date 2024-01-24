package pl.slaszu.unit.analizer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stockanalyzer.HighestPriceSinceFewDays
import pl.slaszu.stockanalyzer.shared.getResourceAsText

class HighestPriceSinceFewDaysTest {
    @Test
    fun `price change more then x percent works`() {
        val priceList = this.getPriceList("10days_highest_price_test.json")

        var logic = HighestPriceSinceFewDays(30, 10)
        var signal = logic.getSignal(priceList)

        println(signal)

        assertEquals(6.75f, signal?.data?.get("lastPrice"))
        assertEquals(5.95f, signal?.data?.get("maxPrice"))


        logic = HighestPriceSinceFewDays(30, 20)
        signal = logic.getSignal(priceList)

        assertEquals(null, signal)
    }

    private fun getPriceList(file: String): Array<StockPriceDto> {
        val resourceAsText = getResourceAsText("/$file")

        val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(resourceAsText, Array<StockPriceDto>::class.java)
    }
}
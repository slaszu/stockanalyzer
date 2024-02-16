package pl.slaszu.unit.analizer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stockanalyzer.HighestPriceFluctuationsSinceFewDays
import pl.slaszu.stockanalyzer.shared.getResourceAsText

class HighestPriceFluctuationsSinceFewDaysTest {
    @Test
    fun `price change more then x percent works`() {
        val priceList = this.getPriceList("10days_no_empty.json")

        var logic = HighestPriceFluctuationsSinceFewDays(30, 10)
        var signal = logic.getSignal(priceList)

        Assertions.assertEquals(20, signal?.data?.get("calculatedPercent")?.toInt())

        logic = HighestPriceFluctuationsSinceFewDays(30, 11)
        signal = logic.getSignal(priceList)

        Assertions.assertEquals(null, signal)
    }

    @Test
    fun `price change more then y percent works with empty days`() {
        val priceList = this.getPriceList("10days_some_empty.json")

        var logic = HighestPriceFluctuationsSinceFewDays(30, 2)
        var signal = logic.getSignal(priceList)

        Assertions.assertEquals(17.39f, signal?.data?.get("calculatedPercent")?.toFloat())
    }

    private fun getPriceList(file: String): Array<StockPriceDto> {
        val resourceAsText = getResourceAsText("/fixtures/$file")

        val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        return objectMapper.readValue(resourceAsText, Array<StockPriceDto>::class.java)
    }
}
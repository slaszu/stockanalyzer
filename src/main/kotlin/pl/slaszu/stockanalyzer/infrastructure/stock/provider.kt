package pl.slaszu.stockanalyzer.infrastructure.stock

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import pl.slaszu.shared_kernel.domain.stock.StockDto
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.shared_kernel.infrastructure.stock.StockApiParams
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.shared_kernel.domain.toUri

@Service
class StockProviderRestTemplate(var restTmp: RestTemplate, var params: StockApiParams) : StockProvider {
    override fun getStockCodeList(): Array<StockDto> {

        val value = this.restTmp.getForEntity(
            params.url.toUri("/stocks"),
            Array<StockDto>::class.java
        );
        return value.body ?: emptyArray<StockDto>();
    }

    override fun getStockPriceList(stockCode: String): Array<StockPriceDto> {
        val value = this.restTmp.getForEntity(
            params.url.toUri("/stocks/prices/$stockCode"),
            Array<StockPriceDto>::class.java
        );
        return value.body ?: emptyArray<StockPriceDto>();
    }
}
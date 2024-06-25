package pl.slaszu.recommendation.infrastructure

import kotlinx.datetime.LocalDate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import pl.slaszu.recommendation.domain.StockProvider
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.shared_kernel.infrastructure.stock.StockApiParams
import pl.slaszu.shared_kernel.domain.toUri

@Service
class RecommendationStockProvider(var restTmp: RestTemplate, var params: StockApiParams) : StockProvider {

    override fun getLastStockPriceList(stockCode: String, dateTo: LocalDate): Array<StockPriceDto> {
        val value = this.restTmp.getForEntity(
            params.url.toUri("/stocks/prices/last/$stockCode/$dateTo"),
            Array<StockPriceDto>::class.java
        );
        return value.body ?: emptyArray<StockPriceDto>();
    }

}
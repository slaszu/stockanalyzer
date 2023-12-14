package pl.slaszu.stockanalyzer.dataprovider.infrastructure

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import pl.slaszu.stockanalyzer.dataprovider.application.StockDto
import pl.slaszu.stockanalyzer.dataprovider.application.StockPriceDto
import pl.slaszu.stockanalyzer.dataprovider.application.StockProvider
import java.net.URI

@Service
class StockProviderRestTemplate(var restTmp: RestTemplate, var params: DataproviderParameters) : StockProvider {
    override fun getStockCodeList(): Array<StockDto> {

        val value = this.restTmp.getForEntity(
            params.stockApiUrl.toUri("/stocks"),
            Array<StockDto>::class.java
        );
        return value.body ?: emptyArray<StockDto>();
    }

    override fun getStockPriceList(stockCode: String): Array<StockPriceDto> {
        val value = this.restTmp.getForEntity(
            params.stockApiUrl.toUri("/stocks/prices/$stockCode"),
            Array<StockPriceDto>::class.java
        );
        return value.body ?: emptyArray<StockPriceDto>();
    }

}

fun String.toUri(path: String): URI {
    return URI.create(this.plus(path))
}
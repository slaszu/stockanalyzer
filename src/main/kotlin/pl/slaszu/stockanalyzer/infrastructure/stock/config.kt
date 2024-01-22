package pl.slaszu.stockanalyzer.infrastructure.stock

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "stock-api")
data class StockApiParams(val url: String) {

}
package pl.slaszu.stockanalyzer.infrastructure.stock

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@ConfigurationProperties(prefix = "stock-api")
data class StockApiParams(val url: String) {

}

@Configuration("stockBeans")
class Beans {

    @Bean
    fun getRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}
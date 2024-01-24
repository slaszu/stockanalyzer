package pl.slaszu.stockanalyzer.domain.stockanalyzer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SignalBeans {

    @Bean
    fun highestPriceFluctuationsSince10Days(): HighestPriceFluctuationsSinceFewDays {
        return HighestPriceFluctuationsSinceFewDays(15,2)
    }

    @Bean
    fun highestPriceSince10Days(): HighestPriceSinceFewDays {
        return HighestPriceSinceFewDays(15, 2)
    }

}
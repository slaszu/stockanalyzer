package pl.slaszu.stockanalyzer.domain.stockanalyzer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SignalBeans {

    @Bean
    fun get2fromLast30(): PriceChangeMoreThenAvg {
        return PriceChangeMoreThenAvg(30,3)
    }

    @Bean
    fun getAthFromLast30(): PriceAthSinceFewDays {
        return PriceAthSinceFewDays(30, 2)
    }

}
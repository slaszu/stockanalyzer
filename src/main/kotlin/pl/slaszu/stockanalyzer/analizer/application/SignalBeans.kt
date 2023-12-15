package pl.slaszu.stockanalyzer.analizer.application

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.analizer.application.signal.PriceChangeMoreThenAvg
import java.util.prefs.PreferenceChangeEvent

@Configuration
class SignalBeans {

    @Bean
    fun get2fromLast5(): PriceChangeMoreThenAvg {
        return PriceChangeMoreThenAvg(2,5)
    }

    @Bean
    fun get2fromLast10(): PriceChangeMoreThenAvg {
        return PriceChangeMoreThenAvg(2,10)
    }

    @Bean
    fun get2fromLast30(): PriceChangeMoreThenAvg {
        return PriceChangeMoreThenAvg(2,30)
    }

}
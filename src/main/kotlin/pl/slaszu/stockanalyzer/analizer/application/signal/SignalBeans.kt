package pl.slaszu.stockanalyzer.analizer.application.signal

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.analizer.application.signal.PriceAthSinceFewDays
import pl.slaszu.stockanalyzer.analizer.application.signal.PriceChangeMoreThenAvg
import java.util.prefs.PreferenceChangeEvent

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
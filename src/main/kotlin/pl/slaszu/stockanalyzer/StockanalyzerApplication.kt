package pl.slaszu.stockanalyzer

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.dataprovider.application.StockProvider
import pl.slaszu.stockanalyzer.dataprovider.infrastructure.DataproviderParameters as DataproviderParameters

@SpringBootApplication
@EnableConfigurationProperties(
    DataproviderParameters::class
)

class StockanalyzerApplication

fun main(args: Array<String>) {
    runApplication<StockanalyzerApplication>(*args)

    println("Hello world")
}

@Configuration
class SomeBeans {
    @Bean
    fun appRunner(rest: StockProvider): ApplicationRunner {
        return ApplicationRunner {
            rest.getStockCodeList().forEach {
                println(it)
                if (it.code != null) {
                    rest.getStockPriceList(it.code).forEach { it1 -> println(it1) }
                    return@ApplicationRunner
                }
            }
        }
    }
}
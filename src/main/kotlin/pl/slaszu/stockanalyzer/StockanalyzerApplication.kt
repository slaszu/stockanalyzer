package pl.slaszu.stockanalyzer

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.analizer.application.SignalProvider
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
    fun appRunner(stockProvider: StockProvider, signalProvider: SignalProvider): ApplicationRunner {
        return ApplicationRunner {
            stockProvider.getStockCodeList().forEach {
                if (it.code != null ) { //&& listOf("CPL","PLW","PCO","CMR").contains(it.code)) {
                    val stockPriceList = stockProvider.getStockPriceList(it.code);
                    val signals = signalProvider.getSignals(stockPriceList);
                    if (signals.isNotEmpty()) {
                        println(it.code)
                        signals.forEach { signal -> println("+$signal") }
                        return@forEach
                    }
                }
            }
        }
    }
}
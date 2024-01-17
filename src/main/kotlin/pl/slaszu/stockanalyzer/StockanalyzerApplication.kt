package pl.slaszu.stockanalyzer

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.insert
import pl.slaszu.stockanalyzer.analizer.application.SignalProvider
import pl.slaszu.stockanalyzer.chart.application.ChartProvider
import pl.slaszu.stockanalyzer.dataprovider.application.StockProvider
import pl.slaszu.stockanalyzer.shared.TestObject
import java.time.LocalDate
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
//    @Bean
//    fun appRunner(stockProvider: StockProvider, signalProvider: SignalProvider): ApplicationRunner {
//        return ApplicationRunner {
//            stockProvider.getStockCodeList().forEach {
//                if (it.code != null ) { //&& listOf("CPL","PLW","PCO","CMR").contains(it.code)) {
//                    val stockPriceList = stockProvider.getStockPriceList(it.code);
//                    val signals = signalProvider.getSignals(stockPriceList);
//                    if (signals.isNotEmpty()) {
//                        println(it.code)
//                        signals.forEach { signal -> println("+$signal") }
//                        return@forEach
//                    }
//                }
//            }
//        }
//    }

//    @Bean
//    fun getChart(stockProvider: StockProvider, chartProvider: ChartProvider): ApplicationRunner {
//
//        return ApplicationRunner {
//            val stockPriceList = stockProvider.getStockPriceList("PLW")
//            val chartAsBase64 = chartProvider.getChartAsBase64("PLW", stockPriceList)
//
//            println(chartAsBase64)
//        }
//    }

    @Bean
    fun mongodbInsert(mongoTemplate: MongoTemplate): ApplicationRunner {
        return ApplicationRunner {
            val x = TestObject(5, "some description", LocalDate.now())
            val y = mongoTemplate.insert(x)

            println(x)
            println(y)

            val res = mongoTemplate.findAll(TestObject::class.java)

            res.forEach(
                ::println
            )
        }
    }
}
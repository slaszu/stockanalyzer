package pl.slaszu.stockanalyzer

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import pl.slaszu.stockanalyzer.analizer.application.SignalEnum
import pl.slaszu.stockanalyzer.dataprovider.infrastructure.DataproviderParameters
import pl.slaszu.stockanalyzer.shared.TestObject
import pl.slaszu.stockanalyzer.shared.toDate
import kotlin.random.Random

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
            val x = TestObject(
                "PLW",
                Random.nextDouble(300.00, 400.00).toFloat(),
                arrayOf(SignalEnum.PRICE_HIGHEST, SignalEnum.PRICE_CHANGE_MORE_THEN_AVG_PERCENT)
            )
            val y = mongoTemplate.insert(x)

            println(x)
            println(y)

            var res = mongoTemplate.find(
                Query.query(
                    Criteria.where("date").lte(
                        LocalDate(2024, 1, 18).toJavaLocalDate().toDate()
                    )
                ),
                TestObject::class.java
            )

            println("find by date")
            res.forEach(
                ::println
            )
            res = mongoTemplate.find(
                Query.query(Criteria.where("price").lte(350)),
                TestObject::class.java
            )

            println("find by price")
            res.forEach(
                ::println
            )

        }
    }
}
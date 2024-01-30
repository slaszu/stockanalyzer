package pl.slaszu.stockanalyzer

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import pl.slaszu.stockanalyzer.application.GetStocksFromApiAnalyzeSignalLogicAndCreateAlerts
import pl.slaszu.stockanalyzer.infrastructure.stock.StockApiParams

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties(
    StockApiParams::class
)
class StockanalyzerApplication

fun main(args: Array<String>) {
    runApplication<StockanalyzerApplication>(*args)

    println("Hello world")
}

@Configuration
@Profile("dev")
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

//    @Bean
//    fun mongodbInsert(mongoTemplate: MongoTemplate): ApplicationRunner {
//        return ApplicationRunner {
//            val x = TestObject(
//                "PLW",
//                Random.nextDouble(300.00, 400.00).toFloat(),
//                listOf(SignalEnum.PRICE_HIGHEST, SignalEnum.PRICE_CHANGE_MORE_THEN_AVG_PERCENT)
//            )
//            val y = mongoTemplate.insert(x)
//
//            println(x)
//            println(y)
//
//            var res = mongoTemplate.find(
//                Query.query(
//                    Criteria.where("date").lte(
//                        LocalDate(2024, 1, 18).toJavaLocalDate().toDate()
//                    )
//                ),
//                TestObject::class.java
//            )
//
//            println("find by date")
//            res.forEach(
//                ::println
//            )
//            res = mongoTemplate.find(
//                Query.query(Criteria.where("price").lte(350)),
//                TestObject::class.java
//            )
//
//            println("find by price")
//            res.forEach(
//                ::println
//            )
//
//        }
//    }
//
//    @Bean
//    fun mongoRepo(repo: TestRepository): ApplicationRunner {
//        return ApplicationRunner {
//
//
//            //val testObj = repo.findOneByStockCode("PLW")
//
//            //println(testObj)
//
//            repo.findAll().forEach(
//                ::println
//            )
//        }
//    }


    @Bean
    fun test(action: GetStocksFromApiAnalyzeSignalLogicAndCreateAlerts): ApplicationRunner {
        return ApplicationRunner {
            action.run()
        }
    }
}
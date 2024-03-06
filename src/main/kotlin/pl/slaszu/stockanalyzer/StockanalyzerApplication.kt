package pl.slaszu.stockanalyzer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import pl.slaszu.stockanalyzer.application.CloseAlerts
import pl.slaszu.stockanalyzer.application.CreateAlerts
import pl.slaszu.stockanalyzer.domain.model.AlertRepository
import pl.slaszu.stockanalyzer.infrastructure.stock.StockApiParams
import pl.slaszu.stockanalyzer.infrastructure.twitter.TwitterConfig

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties(
    StockApiParams::class, TwitterConfig::class
)
@EnableScheduling
class StockanalyzerApplication

fun main(args: Array<String>) {
    runApplication<StockanalyzerApplication>(*args)

    println("Hello world")
}

@Configuration
class ProdBeans {
    @Bean
    fun testMongoConnection(alertRepo: AlertRepository, @Value("\${spring.data.mongodb.uri}") mongoUrl: String): ApplicationRunner {
        return ApplicationRunner {
            println("$mongoUrl")
            val findAll = alertRepo.findAll()
            println("Mongo test, alert models find ${findAll.size} qty")
        }
    }
}

@Configuration
@Profile("default")
class SomeBeans {


//    @Bean
//    fun testCreate(action: CreateAlerts): ApplicationRunner {
//        return ApplicationRunner {
//            action.run()
//        }
//    }

}
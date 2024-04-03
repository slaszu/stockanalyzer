package pl.slaszu.stockanalyzer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import pl.slaszu.stockanalyzer.application.CloseAlerts
import pl.slaszu.stockanalyzer.application.CreateReport
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.infrastructure.stock.StockApiParams
import pl.slaszu.stockanalyzer.infrastructure.publisher.TwitterConfig

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
    fun testMongoConnection(
        buildProperty: BuildProperties,
        alertRepo: AlertRepository,
        @Value("\${spring.data.mongodb.uri}") mongoUrl: String
    ): ApplicationRunner {
        return ApplicationRunner {

            buildProperty.forEach {
                println("${it.key} => ${it.value}")
            }

            println("$mongoUrl")
            val findAll = alertRepo.findAll()
            println("Mongo test, alert models find ${findAll.size} qty")
        }
    }
}

@Configuration
@Profile("default")
class SomeBeans {

    @Bean
    fun testCreate(action: CreateReport): ApplicationRunner {
        return ApplicationRunner {
            action.runForDaysAfter(14)
        }
    }



}
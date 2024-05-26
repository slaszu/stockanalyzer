package pl.slaszu.stockanalyzer

import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import pl.slaszu.stockanalyzer.application.CreateReport
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.infrastructure.publisher.TwitterConfig
import pl.slaszu.stockanalyzer.infrastructure.stock.StockApiParams

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
    fun sentryInit(): ApplicationRunner {
        return ApplicationRunner {
            Sentry.init { options ->
                options.dsn = "https://91448fb4cf34aae51004514cd4526af3@o74341.ingest.us.sentry.io/4507126154657792"
                // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
                // We recommend adjusting this value in production.
                //options.tracesSampleRate = 1.0

                // When first trying Sentry it's good to see what the SDK is doing:
                options.isDebug = true
            }

            println("sentry init done")
        }
    }

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

    //    @Bean
//    fun testCreateAlert(action: CreateAlerts): ApplicationRunner {
//        return ApplicationRunner {
//            action.run()
//        }
//    }
//
//    @Bean
//    fun testCloseAlert(action: CloseAlerts): ApplicationRunner {
//        return ApplicationRunner {
//            action.runForDaysAfter(0, true)
//        }
//    }
//
    @Bean
    fun testCreateReport(action: CreateReport): ApplicationRunner {
        return ApplicationRunner {
            action.runForDaysAfter(7)
        }
    }


}
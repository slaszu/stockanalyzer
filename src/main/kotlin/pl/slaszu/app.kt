package pl.slaszu

//import io.sentry.Sentry
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
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import pl.slaszu.recommendation.infrastructure.QdrantConfig
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.infrastructure.stock.StockApiParams
import pl.slaszu.stockanalyzer.infrastructure.publisher.TwitterConfig

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties(
    StockApiParams::class, TwitterConfig::class, QdrantConfig::class
)
@EnableScheduling
@EnableAsync
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)

    println("Hello world")
}

@Configuration
class ProdBeans {

//    @Bean
//    fun sentryInit(): ApplicationRunner {
//        return ApplicationRunner {
//            Sentry.init { options ->
//                options.dsn = "https://91448fb4cf34aae51004514cd4526af3@o74341.ingest.us.sentry.io/4507126154657792"
//                // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
//                // We recommend adjusting this value in production.
//                //options.tracesSampleRate = 1.0
//
//                // When first trying Sentry it's good to see what the SDK is doing:
//                options.isDebug = true
//            }
//
//            println("sentry init done")
//        }
//    }

    @Bean
    @Profile("!test")
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
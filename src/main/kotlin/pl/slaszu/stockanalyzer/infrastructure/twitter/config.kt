package pl.slaszu.stockanalyzer.infrastructure.twitter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@ConfigurationProperties(prefix = "twitter-config")
data class TwitterConfig(
    val apiKey: String,
    val apiSecretKey: String,
    val accessToken: String,
    val accessTokenSecret: String
)

@Configuration("twitterBeans")
class Beans {

    @Bean
    fun getTwitterClient(objMapper: ObjectMapper, twitterConfig: TwitterConfig): TwitterClient {

        val logger = KotlinLogging.logger { }
        logger.debug { twitterConfig.toString() }

        val credentials = TwitterCredentials.builder()
            .apiKey(twitterConfig.apiKey)
            .accessToken(twitterConfig.accessToken)
            .apiSecretKey(twitterConfig.apiSecretKey)
            .accessTokenSecret(twitterConfig.accessTokenSecret)
            .build()

        return TwitterClient(credentials)
    }

}
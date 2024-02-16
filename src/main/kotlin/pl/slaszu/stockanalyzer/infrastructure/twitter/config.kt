package pl.slaszu.stockanalyzer.infrastructure.twitter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.slaszu.stockanalyzer.shared.getResourceAsText


@ConfigurationProperties(prefix = "twitter-config")
data class TwitterConfig(val credentialsFile: String) {

}

@Configuration("twitterBeans")
class Beans {

    @Bean
    fun getTwitterClient(objMapper: ObjectMapper, twitterConfig: TwitterConfig): TwitterClient {

        val jsonString = getResourceAsText(twitterConfig.credentialsFile)

        val credentials = objMapper.readValue(jsonString, TwitterCredentials::class.java)

        return TwitterClient(credentials)
    }

}
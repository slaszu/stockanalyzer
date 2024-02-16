package pl.slaszu.stockanalyzer.infrastructure.twitter

import io.github.redouane59.twitter.TwitterClient
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import java.io.File

@Service
class TwitterPublisher(val twitterClient: TwitterClient):Publisher {
    override fun publish(chartImgFile: File, title: String, desc: String): String {
        val postTweet = twitterClient.postTweet("Kotlin test")
        println(postTweet)
        return postTweet.id
    }
}
package pl.slaszu.stockanalyzer.infrastructure.twitter

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.dto.tweet.MediaCategory
import io.github.redouane59.twitter.dto.tweet.TweetParameters
import io.github.redouane59.twitter.dto.tweet.TweetParameters.TweetParametersBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import java.io.File
import kotlin.random.Random


@Service
@Profile(value = ["test","default"])
class TestPublisher(
    val logger: KLogger = KotlinLogging.logger { }
): Publisher
{
    override fun publish(
        pngChartByteArray: ByteArray,
        title: String,
        desc: String,
        quotedPublishedId: String?
    ): String {
        this.logger.warn { "Fake publisher !!! \n$title\n$desc" }
        val randomInt = Random.nextInt(10000, 99999)
        return "fake_publisher_$randomInt"
    }

}

@Service
@Profile("prod")
class TwitterPublisher(
    val twitterClient: TwitterClient,
    val logger: KLogger = KotlinLogging.logger { }
) : Publisher {
    override fun publish(
        pngChartByteArray: ByteArray,
        title: String,
        desc: String,
        quotedPublishedId: String?
    ): String {

        val uploadMediaResponse = twitterClient.uploadMedia(
            "stock_alert",
            pngChartByteArray,
            MediaCategory.TWEET_IMAGE
        );

        val tweetParametersBuilder = TweetParameters.builder()
            .text("$title\n$desc")
            .media(
                TweetParameters.Media.builder().mediaIds(listOf(uploadMediaResponse.mediaId)).build()
            )

        if (quotedPublishedId != null)
            tweetParametersBuilder.quoteTweetId(quotedPublishedId)

        try {
            val postTweet = twitterClient.postTweet(tweetParametersBuilder.build())
            return postTweet.id
        } catch (e: Throwable) {
            this.logger.error(e) { "Twitter problem for tweet : $tweetParametersBuilder" }
            throw e
        }
    }


}
package pl.slaszu.stockanalyzer.domain.publisher

import com.twitter.twittertext.TwitterTextParser

interface Publisher {
    fun publish(
        pngChartByteArray: ByteArray,
        title: String,
        desc: String,
        quotedPublishedId: String? = null
    ): String

    fun publish(
        pngList: List<ByteArray>,
        title: String,
        desc: String,
        quotedPublishedId: String? = null
    ): String

    fun checkText(text: String): String {
        val parseTweet = TwitterTextParser.parseTweet(text)
        if (!parseTweet.isValid) {
            println("Text is no valid ! Text length: '${parseTweet.weightedLength}' dose not to fit to range '${parseTweet.validTextRange.start} to ${parseTweet.validTextRange.end}'")
            val lessText = text.substringBeforeLast("\n")
            if (lessText.length == text.length) {
                throw Exception("$text has same size as $lessText !")
            }
            return checkText(lessText);
        }
        println("Text length: '${parseTweet.weightedLength}' fit to range '${parseTweet.validTextRange.start} to ${parseTweet.validTextRange.end}'")
        return text
    }
}
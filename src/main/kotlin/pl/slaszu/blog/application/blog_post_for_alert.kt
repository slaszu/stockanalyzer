package pl.slaszu.blog.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.blog.domain.BlogPostRepository
import pl.slaszu.blog.domain.PostProvider
import pl.slaszu.blog.domain.PostSignal
import pl.slaszu.recommendation.application.RecommendationForAlert
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.roundTo
import pl.slaszu.stockanalyzer.application.ChartForAlert
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class BlogPostForAlert(
    private val blogClient: BlogClient,
    private val blogPostRepository: BlogPostRepository,
    private val chartForAlert: ChartForAlert,
    private val recommendationForAlert: RecommendationForAlert,
    private val postProvider: PostProvider
) {
    private val logger = KotlinLogging.logger { }

    fun createNewPost(alert: AlertModel): String? {

        val byteArray = this.chartForAlert.getChartPngForAlert(alert) ?: return null
        val base64 = Base64.getEncoder().encodeToString(byteArray)

        val mainSignal = PostSignal(
            alert.getPostTitle(),
            listOf(base64),
            alert.getPostDescription()
        )

        val similarSignals = mutableListOf<PostSignal>()
        val listOfList = this.recommendationForAlert.getCloseAlertModelListOfList(alert)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyy")
        listOfList.forEach { closeAlertList ->
            val alertIn = closeAlertList.firstOrNull()?.alert ?: return@forEach

            val pngByteArray = this.chartForAlert.getChartPngForCloseAlert(closeAlertList)

            similarSignals.add(
                PostSignal(
                    alertIn.getTitle(),
                    listOf(Base64.getEncoder().encodeToString(pngByteArray)),
                    alertIn.date.format(formatter)
                )
            )
        }

        val content = this.postProvider.getHtml(mainSignal, similarSignals)

        var blogPostModel = this.blogClient.insertPost(
            "${alert.stockName} ${alert.getTitle()}",
            content,
            listOf(alert.stockCode)
        )

        blogPostModel = this.blogPostRepository.save(
            blogPostModel.copy(
                alert = alert
            )
        )

        this.logger.debug { "New blog post created: $blogPostModel" }

        return blogPostModel.postUrl
    }
}

fun AlertModel.getPostTitle(): String {
    return "Sygnał kupna dla $stockName cena ${getBuyPrice()}&nbsp;PLN"
}

fun AlertModel.getPostDescription(): String {
    var desc = ""
    predictions.forEach { dayAfter, resultPercent ->
        var sign = "\uD83D\uDD34"
        if (resultPercent > 0) {
            sign = "\uD83D\uDFE2"
        }

        desc += "$sign po $dayAfter dniach średni wynik to ${resultPercent.roundTo(2)}&nbsp;%\n"
    }

    return desc
}

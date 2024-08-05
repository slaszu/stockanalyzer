package pl.slaszu.blog.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.blog.domain.BlogPostRepository
import pl.slaszu.blog.domain.PostProvider
import pl.slaszu.blog.domain.PostSignal
import pl.slaszu.recommendation.application.RecommendationForAlert
import pl.slaszu.shared_kernel.domain.alert.AlertModel
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
            alert.getTitle(),
            listOf(base64),
            alert.getPredicationText()
        )

        val similarSignals = mutableListOf<PostSignal>()
        val listOfList = this.recommendationForAlert.getCloseAlertModelListOfList(alert)
        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")
        listOfList.forEach { closeAlertList ->
            val alertIn = closeAlertList.first().alert

            val pngBase64List = mutableListOf<String>()

            closeAlertList.forEach { closeAlertModel ->
                val byteArrayIn = this.chartForAlert.getChartPngForCloseAlert(closeAlertModel)
                if (byteArrayIn != null) {
                    pngBase64List.add(Base64.getEncoder().encodeToString(byteArrayIn))
                }
            }

            similarSignals.add(
                PostSignal(
                    "${alertIn.stockName} [${alertIn.stockName}]",
                    pngBase64List,
                    "signal date: ${alertIn.date.format(formatter)}"
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
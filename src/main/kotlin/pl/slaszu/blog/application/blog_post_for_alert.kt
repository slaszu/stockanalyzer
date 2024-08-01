package pl.slaszu.blog.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.blog.domain.BlogPostRepository
import pl.slaszu.recommendation.application.RecommendationForAlert
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.stockanalyzer.application.ChartForAlert
import java.util.*

@Service
class BlogPostForAlert(
    private val blogClient: BlogClient,
    private val blogPostRepository: BlogPostRepository,
    private val chartForAlert: ChartForAlert,
    private val recommendationForAlert: RecommendationForAlert
) {
    private val logger = KotlinLogging.logger { }

    fun createNewPost(alert: AlertModel): String {
        val byteArray = this.chartForAlert.getChartPngForAlert(alert)
        this.logger.debug { "Alert ${alert.stockCode} => ${byteArray?.size}" }

        val base64 = Base64.getEncoder().encodeToString(byteArray)

        // todo change to html template
        var content = "${alert.getTitle()}\n" +
                "${alert.getPredicationText()}\n" +
                "<img src=\"data:image/png;base64, $base64\" alt=\"${alert.getTitle()}\" />\n" +
                "similar signals in past:\n"

        val listOfList = this.recommendationForAlert.getCloseAlertModelListOfList(alert)
        listOfList.forEach { closeAlertList ->
            val alert = closeAlertList.first().alert
            content += "${alert.stockName} [${alert.stockCode}]\n"
            closeAlertList.forEach { closeAlertModel ->

                val byteArrayIn = this.chartForAlert.getChartPngForCloseAlert(closeAlertModel)
                this.logger.debug { "Close alert ${alert.stockCode} => ${byteArrayIn?.size}" }

                val base64In = Base64.getEncoder().encodeToString(byteArrayIn)


                content += "<img src=\"data:image/png;base64, $base64In\" width='400px' alt='sell ${closeAlertModel.alert.stockCode}' /> "
            }
        }

        content = content.replace("\n","<br>")


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
package pl.slaszu.blog.infrastructure

import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.model.Post
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.stockanalyzer.application.ChartForAlert
import java.util.*


@Service
class BloggerClient(
    val blogger: Blogger,
    val chart: ChartForAlert
): BlogClient {

    private val logger = KotlinLogging.logger {  }
    override fun insertPost(alertModel: AlertModel) {

        val byteArray = this.chart.getChartPngForAlert(alertModel)
        val base64 = Base64.getEncoder().encodeToString(byteArray)

        val post = Post()
        post.title = alertModel.getTitle()
        post.content = "Content = ${alertModel.getTitle()}\n" +
                "Chart\n" +
                "<img src=\"data:image/png;base64, $base64\" alt=\"Red dot\" />"
        post.labels = listOf(alertModel.stockCode)

        val res = this.blogger.Posts().insert("2989806055464746341", post).execute()

        logger.debug { res.url }

    }

    override fun getPosts() {
        val list = this.blogger.Posts().list("2989806055464746341")
        val postList = list.execute()
        postList.forEach {
            logger.debug { "${it.key} => ${it.value}" }
        }
    }
}
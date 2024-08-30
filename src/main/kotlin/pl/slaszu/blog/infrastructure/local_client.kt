package pl.slaszu.blog.infrastructure

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.blog.domain.BlogPostModel

@Service
@Profile(value = ["test", "default"])
class LocalClient : BlogClient {

    private val logger = KotlinLogging.logger { }

    override fun insertPost(title: String, content: String, labels: List<String>): BlogPostModel {
        this.logger.debug {
            """
            FakeBlogClientPost\n
            title: $title \n
            content has ${content.length} chars \n
            labels: ${labels.joinToString(", ")}
        """.trimIndent()
        }

        return BlogPostModel(
            postId = "fake_post_id",
            postUrl = "fake_post_url",
        )
    }

}
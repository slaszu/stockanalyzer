package pl.slaszu.blog.infrastructure

import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.model.Post
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.BlogClient
import pl.slaszu.blog.domain.BlogPostModel


@Service
@Profile("prod")
class BloggerClient(
    val blogger: Blogger,
    val bloggerConfig: BloggerConfig
) : BlogClient {

    private val logger = KotlinLogging.logger { }
    override fun insertPost(title: String, content: String, labels: List<String>): BlogPostModel {

        val post = Post()
        post.title = title
        post.content = content
        post.labels = labels

        val res = this.blogger.Posts().insert(bloggerConfig.blogId, post).execute()

        logger.debug { "Post id = ${res.id} => ${res.url}" }

        return BlogPostModel(res.id, res.url)
    }
}
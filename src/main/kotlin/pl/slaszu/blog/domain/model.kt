package pl.slaszu.blog.domain

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import java.time.LocalDateTime


@Document("blog_post")
@TypeAlias("blog_post")
data class BlogPostModel(
    val postId: String,
    val postUrl: String,
    val alert: AlertModel? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null,
)


interface BlogPostRepository : MongoRepository<BlogPostModel, String>

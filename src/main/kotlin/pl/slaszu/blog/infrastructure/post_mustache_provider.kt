package pl.slaszu.blog.infrastructure

import com.samskivert.mustache.Mustache
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader
import org.springframework.stereotype.Service
import pl.slaszu.blog.domain.PostProvider
import pl.slaszu.blog.domain.PostSignal
import java.io.File

@Service
class PostMustacheProvider(
    private val templateLoader: MustacheResourceTemplateLoader,
    private val compiler: Mustache.Compiler

) : PostProvider {

    override fun getHtml(mainSignal: PostSignal, signals: List<PostSignal>): String {
        val reader = templateLoader.getTemplate("post")
        val template = compiler.compile(reader)
        val html = template.execute(PostContent(mainSignal, signals))

        File("testing_post.html").writeText(html)

        return html
    }
}

class PostContent(
    val mainSignal: PostSignal,
    val signals: List<PostSignal>
)
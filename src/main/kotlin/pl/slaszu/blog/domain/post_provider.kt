package pl.slaszu.blog.domain

interface PostProvider {
    fun getHtml(
        mainSignal: PostSignal,
        signals: List<PostSignal>
    ): String
}

data class PostSignal(
    val title:String,
    val pngBase64: String?,
    val desc: String?
)
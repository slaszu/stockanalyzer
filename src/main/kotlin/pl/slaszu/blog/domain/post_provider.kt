package pl.slaszu.blog.domain

interface PostProvider {
    fun getHtml(
        mainSignal: PostSignal,
        signals: List<PostSignal>
    ): String
}

data class PostSignal(
    val title:String,
    val pngBase64List: List<String>,
    val desc: String?
) {
    val firstPngBase64: String
        get() = this.pngBase64List.firstOrNull() ?: ""

    val descHtml: String
        get() = this.desc?.replace("\n","<br/>") ?: ""
}
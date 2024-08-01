package pl.slaszu.blog.domain

interface BlogClient {
    fun insertPost(title: String, content: String, labels: List<String>): BlogPostModel
}
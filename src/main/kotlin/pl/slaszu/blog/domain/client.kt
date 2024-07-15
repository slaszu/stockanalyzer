package pl.slaszu.blog.domain

import pl.slaszu.shared_kernel.domain.alert.AlertModel

interface BlogClient {
    fun insertPost(alertModel: AlertModel)

    fun getPosts()
}
package pl.slaszu.stockanalyzer.domain.publisher

import java.io.File

interface Publisher {
    fun publish(chartImgFile: File, title: String, desc: String): String
}
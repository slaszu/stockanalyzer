package pl.slaszu.stockanalyzer.domain.publisher

import java.io.File

interface Publisher {
    fun publish(pngChartByteArray: ByteArray, title: String, desc: String, quotedPublishedId: String? = null): String
}
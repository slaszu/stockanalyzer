package pl.slaszu.stockanalyzer.infrastructure.publisher

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.toFile
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import kotlin.random.Random


@Service
@Profile(value = ["test", "default"])
class TestPublisher(
    val logger: KLogger = KotlinLogging.logger { }
) : Publisher {
    override fun publish(
        pngChartByteArray: ByteArray,
        title: String,
        desc: String,
        quotedPublishedId: String?
    ): String {

        val randomInt = Random.nextInt(10000, 99999)
        val publisherId = "fake_publisher_$randomInt"

        val text = this.checkText("$title\n$desc")

        val filePath = pngChartByteArray.toFile("chart_$publisherId.png")

        this.logger.warn { "Fake publisher !!! \n$text\nchart=>$filePath" }

        return publisherId;
    }

    override fun publish(
        pngList: List<ByteArray>,
        title: String,
        desc: String,
        quotedPublishedId: String?
    ): String {
        val randomInt = Random.nextInt(10000, 99999)
        val publisherId = "fake_publisher_$randomInt"

        val text = this.checkText("$title\n$desc")

        val files: MutableList<String> = mutableListOf()
        pngList.take(4).forEachIndexed { index, bytes ->

            val fileName = "chart_$publisherId-$index.png"

            files.add(bytes.toFile(fileName))
        }

        this.logger.warn { "Fake publisher !!! \n$text\nchart=>${files.joinToString { "$it" }}" }

        return publisherId;
    }

}

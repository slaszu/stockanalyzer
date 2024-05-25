package pl.slaszu.stockanalyzer.infrastructure.publisher

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jfree.chart.ChartUtils
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.publisher.Publisher
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.outputStream
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

        val file = Path("chart_$publisherId.png")

        val text = this.checkText("$title\n$desc")

        ChartUtils.writeBufferedImageAsPNG(file.outputStream(), ImageIO.read(ByteArrayInputStream(pngChartByteArray)))

        this.logger.warn { "Fake publisher !!! \n$text\nchart=>${file.toAbsolutePath()}" }

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
        pngList.forEachIndexed { index, bytes ->

            val fileName = "chart_$publisherId-$index.png"
            val file = Path(fileName)

            ChartUtils.writeBufferedImageAsPNG(
                file.outputStream(),
                ImageIO.read(ByteArrayInputStream(bytes))
            )

            files.add(fileName)
        }

        this.logger.warn { "Fake publisher !!! \n$text\nchart=>${files.joinToString { "$it" }}" }

        return publisherId;
    }

}

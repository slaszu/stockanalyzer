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

        ChartUtils.writeBufferedImageAsPNG(file.outputStream(), ImageIO.read(ByteArrayInputStream(pngChartByteArray)))

        this.logger.warn { "Fake publisher !!! \n$title\n$desc\nchart=>${file.toAbsolutePath()}" }

        return publisherId;
    }

}

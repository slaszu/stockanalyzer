package pl.slaszu.stockanalyzer.shared

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import pl.slaszu.stockanalyzer.analizer.application.SignalEnum
import java.time.LocalDate
import java.time.LocalDateTime


@Configuration
class SimpleMongoConfig {
    @Bean
    fun mongo(): MongoClient {
        val connectionString = ConnectionString("mongodb://root:example@localhost:27017/")
        val mongoClientSettings: MongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongo(), "test")
    }
}

@Document("test")
@TypeAlias("test_object")
data class TestObject(
    val stockCode: String,
    val price: Float,
    val signals: Array<SignalEnum>,
    val date: LocalDateTime = LocalDateTime.now(),
    val id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestObject

        if (stockCode != other.stockCode) return false
        if (price != other.price) return false
        if (!signals.contentEquals(other.signals)) return false
        if (date != other.date) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stockCode.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + signals.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
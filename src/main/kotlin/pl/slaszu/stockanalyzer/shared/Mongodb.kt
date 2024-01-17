package pl.slaszu.stockanalyzer.shared

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.LocalDate


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

data class TestObject(val x:Int, val desc:String, val date:LocalDate, val id:String? = null)
package pl.slaszu.blog.infrastructure

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File


@ConfigurationProperties(prefix = "blogger-google-api-oauth")
data class BloggerOauthConfig(
    val tokenStorageDir: String,
    val credentialFileJson: String
)

@Configuration
class BloggerConfiguration {

    val jsonFactory = GsonFactory()
    val httpTransport = NetHttpTransport()


    @Bean
    fun getPreparedBloggerObject(bloggerOauthConfig: BloggerOauthConfig): Blogger {

        val credential = this.getCredentials(bloggerOauthConfig)

        return Blogger.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Blogger-PostsInsert-Snippet/1.0")
            .build()
    }

    private fun getCredentials(bloggerOauthConfig: BloggerOauthConfig): Credential {

        // Load client secrets.
        val clientSecrets = GoogleClientSecrets.load(
            GsonFactory(),
            File(bloggerOauthConfig.credentialFileJson).reader()
        )

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, listOf(BloggerScopes.BLOGGER)
        )
            .setDataStoreFactory(
                FileDataStoreFactory(File(bloggerOauthConfig.tokenStorageDir))
            )
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()

        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        //returns an authorized Credential object.
        return credential
    }

}
package pl.slaszu.blog.infrastructure

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File


@Configuration
class BloggerConfiguration {

    val JSON_FACTORY = GsonFactory()
    val HTTP_TRANSPORT = NetHttpTransport()


    @Bean
    fun getPreparedBloggerObject(): Blogger {

         return Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.getCredentials(HTTP_TRANSPORT))
            .setApplicationName("Blogger-PostsInsert-Snippet/1.0")
            .build()
    }

    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        // Load client secrets.
        val clientSecrets =
            GoogleClientSecrets.load(GsonFactory(), File("/Users/slaszu/Downloads/blogger-service.json").reader())

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, listOf(BloggerScopes.BLOGGER)
        )
            .setDataStoreFactory(MemoryDataStoreFactory())
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        //returns an authorized Credential object.
        return credential
    }

}
package pl.slaszu.stockanalyzer.dataprovider.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("dataprovider")
data class DataproviderParameters(val stockApiUrl: String) {
}
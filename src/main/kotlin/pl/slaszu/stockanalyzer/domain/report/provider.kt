package pl.slaszu.stockanalyzer.domain.report

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel

interface ReportProvider {
    fun getHtml(closeAlertModelList: List<CloseAlertModel>, data: Map<String, String>?): String

    fun getPngByteArray(html: String): ByteArray
}
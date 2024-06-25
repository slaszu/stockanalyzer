package pl.slaszu.stockanalyzer.domain.report

import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel

interface ReportProvider {
    fun getHtml(closeAlertModelList: List<CloseAlertModel>, data: Map<String, String>?): String

    fun getPngByteArray(html: String): ByteArray
}
package pl.slaszu.stockanalyzer.domain.report

import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel

interface ReportProvider {
    fun getHtml(alertList: List<AlertModel>): String

    fun getPngByteArray(alertList: List<AlertModel>): ByteArray
}
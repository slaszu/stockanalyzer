package pl.slaszu.stockanalyzer.domain.alert

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository
import java.time.LocalDateTime

@Service
class CloseAlertService(
    val alertRepo: AlertRepository,
    val closeAlertRepo: CloseAlertRepository
) {
    fun closeAlert(alert: AlertModel) {

        // id must exists
        val alertId = alert.id!!

        val alertRefreshed = alert.copy(close = true, closeDate = LocalDateTime.now())

        val findByAlertIdList = this.closeAlertRepo.findByAlertId(alertId)
        findByAlertIdList.forEach {
            val closeAlertModelRefreshed = it.copy(alert = alertRefreshed)
            this.closeAlertRepo.save(closeAlertModelRefreshed)
        }

        this.alertRepo.save(alertRefreshed)
    }
}
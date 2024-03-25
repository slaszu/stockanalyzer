package pl.slaszu.stockanalyzer.domain.alert

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.AlertRepository
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository

@Service
class CloseAlertService(
    val alertRepo: AlertRepository,
    val closeAlertRepo: CloseAlertRepository
) {
    fun closeAlert(alert: AlertModel) {

        // id must exists
        val alertId = alert.id!!

        alert.close = true
        val findByAlertIdList = this.closeAlertRepo.findByAlertId(alertId)
        findByAlertIdList.forEach {
            it.alert = alert
            this.closeAlertRepo.save(it)
        }

        this.alertRepo.save(alert)
    }
}
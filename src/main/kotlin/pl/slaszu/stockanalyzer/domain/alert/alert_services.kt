package pl.slaszu.stockanalyzer.domain.alert

import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.EventDispatcher
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.AlertRepository
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertRepository
import pl.slaszu.shared_kernel.domain.stock.StockDto
import pl.slaszu.stockanalyzer.domain.event.CreateAlertEvent
import pl.slaszu.stockanalyzer.domain.event.PersistAlertEvent
import java.time.LocalDateTime

@Service
class CloseAlertService(
    private val alertRepo: AlertRepository,
    private val closeAlertRepo: CloseAlertRepository
) {

    fun persistCloseAlert(closeAlert: CloseAlertModel): CloseAlertModel {
        return this.closeAlertRepo.save(closeAlert)
    }

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

@Service
class AlertService(
    private val alertRepo: AlertRepository,
    private val eventDispatcher: EventDispatcher
) {
    fun createAlert(stock: StockDto, price: Float, signals: List<String>): AlertModel {
        val alert = AlertModel(
            stock.code!!,
            stock.name,
            price,
            signals
        )

        val event = CreateAlertEvent(alert)
        this.eventDispatcher.dispatch(event)

        return event.changedAlert ?: alert
    }

    fun persistAlert(alert: AlertModel): AlertModel {
        return this.alertRepo.save(alert).also {
            val event = PersistAlertEvent(it)
            this.eventDispatcher.dispatch(event)
        }
    }
}
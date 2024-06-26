package pl.slaszu.stockanalyzer.domain.event

import pl.slaszu.shared_kernel.domain.alert.AlertModel

data class CreateAlertEvent(
    val createdAlert: AlertModel
) {
    var changedAlert: AlertModel? = null
}

data class PersistAlertEvent(
    val alert: AlertModel
)
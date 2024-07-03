package pl.slaszu.stockanalyzer.domain.event

import pl.slaszu.shared_kernel.domain.alert.AlertModel

data class CreateAlertEvent(
    val createdAlert: AlertModel
) {
    var changedAlert: AlertModel? = null
}

data class PersistAlertBeforeEvent(
    val alert: AlertModel
) {
    val changedAlert: AlertModel? = null
}

data class PersistAlertAfterEvent(
    val alert: AlertModel
)
package pl.slaszu.stockanalyzer.domain

import pl.slaszu.shared_kernel.domain.alert.AlertModel

class CreateAlertEvent(
    val createdAlert: AlertModel
) {
    val changedAlert: AlertModel? = null
}
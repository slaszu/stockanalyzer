package pl.slaszu.stockanalyzer.domain.event

import pl.slaszu.shared_kernel.domain.alert.AlertModel

class CreateAlertEvent(
    val createdAlert: AlertModel
) {
    var changedAlert: AlertModel? = null
}
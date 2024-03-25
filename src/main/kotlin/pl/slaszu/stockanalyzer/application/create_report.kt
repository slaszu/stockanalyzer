package pl.slaszu.stockanalyzer.application

import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertRepository

class CreateReport(
    private val closeAlertRepository: CloseAlertRepository
) {

    fun runForDaysAfter(daysAfter: Int) {

        /**
         * 1. get close_alert from last daysAfter
         */

        this.closeAlertRepository.findByDaysAfterAndAlertClose(daysAfter)

    }

}
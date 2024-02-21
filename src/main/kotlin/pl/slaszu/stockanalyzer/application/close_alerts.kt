package pl.slaszu.stockanalyzer.application

class CloseAlerts() {
    fun run() {
        // todo close alert service
        // get alerts not close and older then x days (7 days)

        // close this alerts by:
        // - get stock prices
        // - get change between open alerts and now
        // - publish tweet with change and png and buy/colse signals
        // - save alert on mongo
    }
}
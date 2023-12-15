package pl.slaszu.stockanalyzer.app

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import pl.slaszu.stockanalyzer.analizer.application.SignalProvider
import pl.slaszu.stockanalyzer.dataprovider.application.StockProvider

@Controller

class HomeController(val signalProvider:SignalProvider) {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = "Blog"
        return "home"
    }

    @GetMapping("/run")
    fun run(model: Model, stockProvider:StockProvider): String {

        // fore each stock
        // - get prices
        // - search signals
        // - if any signal, create chart
        // - save chart as file, add entity do db
        // - display all signals on page www

        stockProvider.getStockCodeList().forEach {
            println(it)
            if (it.code != null) {
                val stockPriceList = stockProvider.getStockPriceList(it.code);
                val signals = this.signalProvider.getSignals(stockPriceList);
                if (signals.isNotEmpty()) {
                    signals.forEach { signal -> println(signal) }
                    return@forEach
                }
            }
        }


        return "run";
    }

}
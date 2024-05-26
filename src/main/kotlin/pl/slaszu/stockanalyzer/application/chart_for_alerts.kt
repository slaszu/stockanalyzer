package pl.slaszu.stockanalyzer.application

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.AlertModel
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.stock.StockProvider
import pl.slaszu.stockanalyzer.shared.roundTo

@Service
class ChartForAlert(
    val stockProvider: StockProvider,
    val chartProvider: ChartProvider
) {
    private fun getBuyPoint(alert: AlertModel, stockPriceList: Array<StockPriceDto>): ChartPoint? {
        val buyPoint = stockPriceList.find {
            it.date == alert.date.toLocalDate()
        }

        if (buyPoint == null) {
            return null;
        }

        val stockCode = alert.stockCode
        val buyPrice = alert.price.roundTo(2)

        return ChartPoint(
            buyPoint,
            buyPrice,
            "BUY ${stockCode} $buyPrice PLN"
        )
    }

    private fun getSellPoint(closeAlert: CloseAlertModel, stockPriceList: Array<StockPriceDto>): ChartPoint? {
        val sellPoint = stockPriceList.find {
            it.date == closeAlert.date.toLocalDate()
        }

        if (sellPoint == null) {
            return null;
        }

        val stockCode = closeAlert.alert.stockCode
        val sellPrice = closeAlert.price?.roundTo(2) ?: return null

        return ChartPoint(
            sellPoint,
            sellPrice,
            "SELL ${stockCode} $sellPrice PLN"
        )
    }

    fun getChartPngForAlert(alert: AlertModel): ByteArray? {
        val stockPriceList = this.stockProvider.getStockPriceList(alert.stockCode)

        val buyPoint = this.getBuyPoint(alert, stockPriceList) ?: return null

        return this.chartProvider.getPngByteArray(
            alert.stockCode,
            stockPriceList,
            buyPoint
        )
    }

    fun getChartPngForCloseAlert(closeAlert: CloseAlertModel): ByteArray? {
        val alert = closeAlert.alert
        val stockPriceList = this.stockProvider.getStockPriceList(alert.stockCode)

        val buyPoint = this.getBuyPoint(alert, stockPriceList) ?: return null

        val sellPoint = this.getSellPoint(closeAlert, stockPriceList) ?: return null

        return this.chartProvider.getPngByteArray(
            alert.stockCode + " ${closeAlert.resultPercent} % (after ${closeAlert.daysAfter} days)",
            stockPriceList,
            buyPoint,
            sellPoint
        )
    }
}
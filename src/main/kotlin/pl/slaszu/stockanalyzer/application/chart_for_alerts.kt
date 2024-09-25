package pl.slaszu.stockanalyzer.application

import kotlinx.datetime.toKotlinLocalDate
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.alert.AlertModel
import pl.slaszu.shared_kernel.domain.alert.CloseAlertModel
import pl.slaszu.shared_kernel.domain.stock.StockPriceDto
import pl.slaszu.stockanalyzer.domain.chart.ChartBuilder
import pl.slaszu.stockanalyzer.domain.chart.ChartPoint
import pl.slaszu.stockanalyzer.domain.chart.ChartProvider
import pl.slaszu.stockanalyzer.domain.stock.StockProvider

@Service
class ChartForAlert(
    val stockProvider: StockProvider,
    val chartProvider: ChartProvider
) {
    fun getBuyPoint(alert: AlertModel, stockPriceList: Array<StockPriceDto>): ChartPoint? {
        val buyPoint = stockPriceList.find {
            it.date == alert.date.toLocalDate()
        }

        if (buyPoint == null) {
            return null;
        }

        return ChartPoint(
            buyPoint,
            alert.getBuyPrice(),
            "BUY ${alert.getBuyPrice()} PLN"
        )
    }

    fun getSellPoint(closeAlert: CloseAlertModel, stockPriceList: Array<StockPriceDto>): ChartPoint? {
        val sellPoint = stockPriceList.find {
            it.date == closeAlert.date.toLocalDate()
        }

        if (sellPoint == null) {
            return null;
        }

        val sellPrice = closeAlert.getClosePrice() ?: return null

        return ChartPoint(
            sellPoint,
            sellPrice,
            "SELL ${closeAlert.getClosePrice()} PLN (${closeAlert.daysAfter} days)"
        )
    }

    fun getChartPngForAlert(alert: AlertModel): ByteArray? {
        val stockPriceList = this.stockProvider.getLastStockPriceList(
            alert.stockCode,
            alert.date.toLocalDate().toKotlinLocalDate()
        )

        val buyPoint = this.getBuyPoint(alert, stockPriceList) ?: return null

        return ChartBuilder.create(this.chartProvider) {
            this.alert = alert
            this.buyPoint = buyPoint
            this.stockPriceList = stockPriceList
        }.getPng()
    }

    fun getChartPngForCloseAlert(closeAlert: CloseAlertModel): ByteArray? {
        val alert = closeAlert.alert
        val stockPriceList = this.stockProvider.getLastStockPriceList(
            alert.stockCode,
            closeAlert.date.toLocalDate().toKotlinLocalDate()
        )

        val buyPoint = this.getBuyPoint(alert, stockPriceList) ?: return null

        val sellPoint = this.getSellPoint(closeAlert, stockPriceList) ?: return null

        return ChartBuilder.create(this.chartProvider) {
            this.closeAlert = closeAlert
            this.buyPoint = buyPoint
            this.closePoint = sellPoint
            this.stockPriceList = stockPriceList
        }.getPng()
    }

    fun getChartPngForCloseAlert(closeAlertList: List<CloseAlertModel>): ByteArray? {

        require(closeAlertList.groupBy { closeAlert -> closeAlert.alert }.size == 1) {
            "All closeAlerts must be associated with the same alert !"
        }

        val closeAlertBiggest = closeAlertList.maxBy { closeAlert -> closeAlert.daysAfter }

        val alert = closeAlertBiggest.alert
        val stockPriceList = this.stockProvider.getLastStockPriceList(
            alert.stockCode,
            closeAlertBiggest.date.toLocalDate().toKotlinLocalDate()
        )

        val buyPoint = this.getBuyPoint(alert, stockPriceList) ?: return null

        val sellPointList = mutableListOf<ChartPoint>()
        closeAlertList
            .sortedBy { closeAlert -> closeAlert.daysAfter }
            .forEach { closeAlert ->
                val sellPoint = this.getSellPoint(closeAlert, stockPriceList)
                if (sellPoint != null) {
                    sellPointList.add(sellPoint)
                }
            }

        return ChartBuilder.create(this.chartProvider) {
            this.alert = alert
            this.buyPoint = buyPoint
            this.closePointList = sellPointList
            this.stockPriceList = stockPriceList
        }.getPng()
    }

}
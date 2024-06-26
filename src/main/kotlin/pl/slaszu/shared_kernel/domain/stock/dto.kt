package pl.slaszu.shared_kernel.domain.stock

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

data class StockDto(val name:String, val code:String?) {
}

data class StockPriceDto(
    val priceOpen: Float,
    val priceHigh: Float,
    val priceLow: Float,
    val price: Float,
    val volume: Int,
    val amount: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss Z")
    val updatedAt: LocalDateTime
) {
}
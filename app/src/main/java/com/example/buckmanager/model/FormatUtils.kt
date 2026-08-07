package com.example.buckmanager.model

fun formatRp(amount: Double): String {
    val locale = if (CurrencyConfig.currencyCode == "IDR") java.util.Locale("id", "ID") else java.util.Locale("en", "US")
    val formatter = java.text.NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = 0
    return "${CurrencyConfig.symbol}${formatter.format(amount)}"
}

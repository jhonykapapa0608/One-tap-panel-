package com.example.onetappanel.util

import java.util.Locale

enum class CurrencyCode(val symbol: String, val displayName: String, val rateFromINR: Double) {
    INR("₹", "INR - Indian Rupee", 1.0),
    USD("$", "USD - US Dollar", 0.012),
    EUR("€", "EUR - Euro", 0.011),
    GBP("£", "GBP - British Pound", 0.0094),
    AED("AED ", "AED - UAE Dirham", 0.044),
    SAR("SAR ", "SAR - Saudi Riyal", 0.045),
    PKR("Rs ", "PKR - Pakistani Rupee", 3.34),
    BDT("৳", "BDT - Bangladeshi Taka", 1.41),
    NPR("Rs ", "NPR - Nepalese Rupee", 1.60);

    companion object {
        fun fromCode(code: String): CurrencyCode {
            return entries.find { it.name.equals(code, ignoreCase = true) } ?: INR
        }
    }
}

object CurrencyUtils {
    fun convertFromINR(amountINR: Double, targetCurrency: String): Double {
        val curr = CurrencyCode.fromCode(targetCurrency)
        return amountINR * curr.rateFromINR
    }

    fun convertToINR(amount: Double, sourceCurrency: String): Double {
        val curr = CurrencyCode.fromCode(sourceCurrency)
        if (curr.rateFromINR == 0.0) return amount
        return amount / curr.rateFromINR
    }

    fun format(amountINR: Double, currencyCodeStr: String): String {
        val curr = CurrencyCode.fromCode(currencyCodeStr)
        val converted = convertFromINR(amountINR, currencyCodeStr)
        return String.format(Locale.US, "%s%.2f", curr.symbol, converted)
    }

    fun formatDirect(amount: Double, currencyCodeStr: String): String {
        val curr = CurrencyCode.fromCode(currencyCodeStr)
        return String.format(Locale.US, "%s%.2f", curr.symbol, amount)
    }
}

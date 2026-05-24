package com.example.onemarket.data.local

data class CardModel(
    val id: Long = System.currentTimeMillis(),
    val cardNumber: String,       // "4111 1111 1111 8139" formatted
    val expiry: String,           // "12/25"
    val cvv: String = "",         // "123"
    val holderName: String = "",
    val cardType: String,         // "ONLINE" or "BIRBANK"
    val addedAt: Long = System.currentTimeMillis()
) {
    val maskedNumber: String
        get() {
            val digits = cardNumber.replace(" ", "")
            return if (digits.length >= 4) "**** **** **** ${digits.takeLast(4)}" else cardNumber
        }

    val last4: String
        get() = cardNumber.replace(" ", "").takeLast(4)

    val typeLabel: String
        get() = if (cardType == "BIRBANK") "Birbank taksit" else "Bank kartı"
}

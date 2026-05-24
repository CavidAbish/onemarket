package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_cards", Context.MODE_PRIVATE)

    fun getCards(): List<CardModel> {
        val json = prefs().getString("cards", null) ?: return emptyList()
        val type = object : TypeToken<List<CardModel>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveCard(card: CardModel) {
        val list = getCards().toMutableList()
        // Eyni kart nömrəsi varsa yenilə, yoxdursa əlavə et
        val existing = list.indexOfFirst {
            it.cardNumber.replace(" ", "") == card.cardNumber.replace(" ", "")
                    && it.cardType == card.cardType
        }
        if (existing >= 0) {
            list[existing] = card.copy(id = list[existing].id, addedAt = list[existing].addedAt)
        } else {
            list.add(card)
        }
        persist(list)
    }

    fun deleteCard(id: Long) {
        val list = getCards().toMutableList()
        list.removeAll { it.id == id }
        persist(list)
    }

    fun getCardById(id: Long): CardModel? = getCards().find { it.id == id }

    fun getCardByType(type: String): CardModel? = getCards().lastOrNull { it.cardType == type }

    fun clearAll() {
        prefs().edit().remove("cards").apply()
    }

    private fun persist(list: List<CardModel>) {
        prefs().edit().putString("cards", gson.toJson(list)).apply()
    }
}

package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_addresses", Context.MODE_PRIVATE)

    fun getAddresses(): List<Address> {
        val json = prefs().getString("addresses", null) ?: return emptyList()
        val type = object : TypeToken<List<Address>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveAddress(address: Address) {
        val list = getAddresses().toMutableList()
        val index = list.indexOfFirst { it.id == address.id }
        if (index >= 0) {
            list[index] = address
        } else {
            list.add(address)
        }
        persist(list)
    }

    fun deleteAddress(id: Long) {
        val list = getAddresses().toMutableList()
        list.removeAll { it.id == id }
        persist(list)
    }

    fun clearAll() {
        prefs().edit().remove("addresses").apply()
    }

    private fun persist(list: List<Address>) {
        prefs().edit().putString("addresses", gson.toJson(list)).apply()
    }
}

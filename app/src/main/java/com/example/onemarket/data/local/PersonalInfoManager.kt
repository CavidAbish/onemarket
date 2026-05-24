package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalInfoManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_personal_info", Context.MODE_PRIVATE)

    fun saveAll(
        firstName: String,
        lastName: String,
        fatherName: String,
        fin: String,
        passport: String,
        birthDate: String,
        gender: String,
        email: String,
        cif: String
    ) {
        prefs().edit()
            .putString("first_name", firstName)
            .putString("last_name", lastName)
            .putString("father_name", fatherName)
            .putString("fin", fin)
            .putString("passport", passport)
            .putString("birth_date", birthDate)
            .putString("gender", gender)
            .putString("email", email)
            .putString("cif", cif)
            .apply()
    }

    fun getFirstName(): String = prefs().getString("first_name", "") ?: ""
    fun getLastName(): String = prefs().getString("last_name", "") ?: ""
    fun getFatherName(): String = prefs().getString("father_name", "") ?: ""
    fun getFin(): String = prefs().getString("fin", "") ?: ""
    fun getPassport(): String = prefs().getString("passport", "") ?: ""
    fun getBirthDate(): String = prefs().getString("birth_date", "") ?: ""
    fun getGender(): String = prefs().getString("gender", "") ?: ""
    fun getEmail(): String = prefs().getString("email", "") ?: ""
    fun getCif(): String = prefs().getString("cif", "") ?: ""

    fun getFullName(): String {
        val first = getFirstName()
        val last = getLastName()
        return when {
            first.isNotEmpty() && last.isNotEmpty() -> "$first $last"
            first.isNotEmpty() -> first
            last.isNotEmpty() -> last
            else -> ""
        }
    }

    fun getOrGenerateCif(phone: String): String {
        val existing = getCif()
        if (existing.isNotEmpty()) return existing
        val seed = phone.filter { it.isDigit() }.toLongOrNull() ?: System.currentTimeMillis()
        val random = java.util.Random(seed)
        val cif = (1000000 + random.nextInt(9000000)).toString()
        prefs().edit().putString("cif", cif).apply()
        return cif
    }

    fun hasPersonalInfo(): Boolean = getFirstName().isNotEmpty() || getLastName().isNotEmpty()

    fun clear() {
        prefs().edit().clear().apply()
    }
}

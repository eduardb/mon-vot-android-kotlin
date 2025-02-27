package ro.code4.monitorizarevot.helper

import android.content.SharedPreferences

const val PREFS_TOKEN = "PREFS_TOKEN"
const val ACCESS_TOKEN = "access_token"
const val PREFS_COUNTY_CODE = "PREFS_COUNTY_CODE"
const val PREFS_BRANCH_NUMBER = "PREFS_BRANCH_NUMBER"
fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.getInt(key: String): Int = getInt(key, 0)

fun SharedPreferences.putString(key: String, value: String) {
    val editor = edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.putInt(key: String, value: Int) {
    val editor = edit()
    editor.putInt(key, value)
    editor.apply()
}


fun SharedPreferences.getToken(): String? = getString(PREFS_TOKEN)
fun SharedPreferences.saveToken(token: String) = putString(PREFS_TOKEN, token)

fun SharedPreferences.saveCountyCode(countyCode: String?) =
    putString(PREFS_COUNTY_CODE, countyCode ?: "")

fun SharedPreferences.getCountyCode(): String? = getString(PREFS_COUNTY_CODE)
fun SharedPreferences.saveBranchNumber(branchNumber: Int) =
    putInt(PREFS_BRANCH_NUMBER, branchNumber)

fun SharedPreferences.getBranchNumber(): Int = getInt(PREFS_BRANCH_NUMBER)
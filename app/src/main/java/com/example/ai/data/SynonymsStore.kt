package com.example.ai.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ai.R

object SynonymsStore {
    private const val PREF_NAME = "synonyms_prefs"
    private const val KEY_JSON = "synonyms_json"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun ensureInitialized(context: Context) {
        val current = prefs(context).getString(KEY_JSON, null)
        if (current.isNullOrBlank()) {
            val raw = context.resources.openRawResource(R.raw.synonyms)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            saveJson(context, raw)
        }
    }

    fun getJson(context: Context): String? {
        return prefs(context).getString(KEY_JSON, null)
    }

    fun saveJson(context: Context, json: String) {
        prefs(context).edit().putString(KEY_JSON, json).apply()
    }
}

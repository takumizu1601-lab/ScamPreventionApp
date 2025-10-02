package com.example.ai.data

import android.content.Context
import org.json.JSONObject
import com.example.ai.R

object SynonymRepository {
    private const val PREF_NAME = "synonyms_prefs"
    private const val KEY_JSON = "synonyms_json"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): Map<String, List<String>> {
        return try {
            val prefs = getPrefs(context)
            val json = prefs.getString(KEY_JSON, null) ?: run {
                val raw = context.resources.openRawResource(R.raw.synonyms)
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
                prefs.edit().putString(KEY_JSON, raw).apply()
                raw
            }
            val obj = JSONObject(json)
            val map = mutableMapOf<String, List<String>>()
            obj.keys().forEach { key ->
                val arr = obj.getJSONArray(key)
                val list = mutableListOf<String>()
                for (i in 0 until arr.length()) list += arr.getString(i)
                map[key] = list
            }
            map
        } catch (e: Exception) {
            // JSON/IOエラー時は空Mapを保存し返す
            val empty = "{}"
            getPrefs(context).edit().putString(KEY_JSON, empty).apply()
            emptyMap()
        }
    }

    fun save(context: Context, map: Map<String, List<String>>?) {
        if (map == null) return
        val obj = JSONObject()
        map.forEach { (k, v) -> obj.put(k, v) }
        getPrefs(context).edit().putString(KEY_JSON, obj.toString()).apply()
    }
}

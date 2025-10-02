package com.example.ai.risk

import android.content.Context
import com.example.ai.data.SynonymRepository

object Detector {
    data class Result(val score: Int, val hits: List<String>, val isHighRisk: Boolean)

    enum class MatchType { EXACT, PARTIAL }

    data class KeywordEntry(
        val word: String,
        val matchType: MatchType,
        val weight: Int
    )

    private var synonymsMap: Map<String, List<String>> = emptyMap()

    fun initSynonyms(context: Context) {
        setSynonyms(SynonymRepository.load(context))
    }

    fun setSynonyms(map: Map<String, List<String>>?) {
        if (map == null || map.isEmpty()) return
        synonymsMap = map
    }

    data class KeywordEntryMatch(val canonical: KeywordEntry, val matchedWord: String)

    private val keywords: List<KeywordEntry> = listOf(
        KeywordEntry("振込", MatchType.PARTIAL, 2),
        KeywordEntry("送金", MatchType.PARTIAL, 2),
        KeywordEntry("詐欺", MatchType.EXACT, 2),
        KeywordEntry("未納", MatchType.PARTIAL, 3),
        KeywordEntry("差押", MatchType.PARTIAL, 3),
        KeywordEntry("裁判", MatchType.PARTIAL, 3),
        KeywordEntry("訴訟", MatchType.PARTIAL, 3),
        KeywordEntry("督促", MatchType.PARTIAL, 3),
        KeywordEntry("至急", MatchType.PARTIAL, 3),
        KeywordEntry("本日中", MatchType.PARTIAL, 3),
        KeywordEntry("期限", MatchType.PARTIAL, 3),
        KeywordEntry("有料サイト", MatchType.PARTIAL, 2),
        KeywordEntry("利用料金", MatchType.PARTIAL, 2),
        KeywordEntry("退会金", MatchType.PARTIAL, 2),
        KeywordEntry("解約金", MatchType.PARTIAL, 2),
        KeywordEntry("還付", MatchType.PARTIAL, 2),
        KeywordEntry("返金", MatchType.PARTIAL, 2),
        KeywordEntry("不在通知", MatchType.PARTIAL, 2),
        KeywordEntry("再配達", MatchType.PARTIAL, 2),
        KeywordEntry("荷物", MatchType.PARTIAL, 2),
        KeywordEntry("配送", MatchType.PARTIAL, 2),
        KeywordEntry("口座", MatchType.PARTIAL, 2),
        KeywordEntry("暗証番号", MatchType.PARTIAL, 2),
        KeywordEntry("認証コード", MatchType.PARTIAL, 2),
        KeywordEntry("本人確認", MatchType.PARTIAL, 2),
        KeywordEntry("身分証", MatchType.PARTIAL, 2),
        KeywordEntry("カード停止", MatchType.PARTIAL, 2),
        KeywordEntry("国税庁", MatchType.PARTIAL, 2),
        KeywordEntry("市役所", MatchType.PARTIAL, 2),
        KeywordEntry("警察", MatchType.PARTIAL, 2),
        KeywordEntry("金融庁", MatchType.PARTIAL, 2),
        KeywordEntry("NHK", MatchType.PARTIAL, 2),
        KeywordEntry("銀行", MatchType.PARTIAL, 2),
        KeywordEntry("ゆうちょ", MatchType.PARTIAL, 2),
        KeywordEntry("bit.ly", MatchType.PARTIAL, 3),
        KeywordEntry("t.co", MatchType.PARTIAL, 3),
        KeywordEntry("tinyurl.com", MatchType.PARTIAL, 3),
        KeywordEntry("ow.ly", MatchType.PARTIAL, 3),
        KeywordEntry("is.gd", MatchType.PARTIAL, 3),
        KeywordEntry("lnkd.in", MatchType.PARTIAL, 3),
        KeywordEntry("x.gd", MatchType.PARTIAL, 3),
        KeywordEntry("v.gd", MatchType.PARTIAL, 3),
        KeywordEntry("こちら", MatchType.PARTIAL, 1),
        KeywordEntry("下記", MatchType.PARTIAL, 1),
        KeywordEntry("次の", MatchType.PARTIAL, 1),
        KeywordEntry("リンク", MatchType.PARTIAL, 1),
        KeywordEntry("URL", MatchType.PARTIAL, 1),
        KeywordEntry("アクセス", MatchType.PARTIAL, 1),
        KeywordEntry("ログイン", MatchType.PARTIAL, 1)
    )

    const val DEFAULT_THRESHOLD = 5

    fun evaluate(text: String): Result {
        var score = 0
        val hits = mutableListOf<String>()

        val normalizedText = text

        val expandedEntries: List<KeywordEntryMatch> = buildList {
            keywords.forEach { entry ->
                add(KeywordEntryMatch(entry, entry.word))
                val syns = synonymsMap[entry.word] ?: emptyList()
                syns.forEach { syn -> add(KeywordEntryMatch(entry, syn)) }
            }
        }

        expandedEntries.forEach { candidate ->
            val matched = when (candidate.canonical.matchType) {
                MatchType.PARTIAL -> normalizedText.contains(candidate.matchedWord, ignoreCase = true)
                MatchType.EXACT -> normalizedText.equals(candidate.matchedWord, ignoreCase = true)
            }
            if (matched) {
                score += candidate.canonical.weight
                val tag = if (candidate.matchedWord == candidate.canonical.word) {
                    "${candidate.canonical.word}:${candidate.canonical.matchType.name.lowercase()}"
                } else {
                    "${candidate.matchedWord}->${candidate.canonical.word}:${candidate.canonical.matchType.name.lowercase()}"
                }
                hits += tag
            }
        }

        var isHighRisk = hits.count { !it.contains("punctuation_boost") } >= 2
        if (isHighRisk) score += 2

        val punctuationBoost = if (text.count { it in "!！?" } >= 3) 1 else 0
        score += punctuationBoost
        if (punctuationBoost > 0) hits += "punctuation_boost"

        return Result(score, hits, isHighRisk)
    }
}

package com.example.ai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 検知結果を保持するエンティティ
 * - SMSはプライバシー配慮のため全文保存しない方針
 *   （高リスクのみ履歴に残す）
 * - Gmail, Manual入力などは全文保存
 */
@Entity(tableName = "detectionLogs")
data class DetectionEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 主キー
    val text: String,        // 解析対象の本文
    val score: Int,          // リスクスコア（0〜100）
    val timestamp: Long = System.currentTimeMillis(), // 登録時刻
    val source: String       // "SMS" / "Gmail" / "Phone" / "Manual"
)

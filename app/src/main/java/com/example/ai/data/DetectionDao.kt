package com.example.ai.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {

    // 検知イベントを追加
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: DetectionEvent)

    // 履歴を取得（新しい順に並べ替え）
    @Query("SELECT * FROM detectionLogs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<DetectionEvent>>

    // 全件削除（デバッグ用やリセット時に使用）
    @Query("DELETE FROM detectionLogs")
    suspend fun clearAll()
}

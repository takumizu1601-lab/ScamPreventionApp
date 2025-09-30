package com.example.ai.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * アプリ全体のRoomデータベース
 * - detectionLogs テーブルを管理
 * - DAOは DetectionDao を提供
 */
@Database(
    entities = [DetectionEvent::class],
    version = 1,               // 今回は初期版なので 1
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao
}

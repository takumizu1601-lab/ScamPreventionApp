package com.example.ai.repo

import com.example.ai.ScamApp
import com.example.ai.data.DetectionDao
import com.example.ai.data.DetectionEvent
import kotlinx.coroutines.flow.Flow

class AppRepository {

    private val dao: DetectionDao = ScamApp.db.detectionDao()

    // 履歴取得（Flowで監視可能）
    fun getRecentLogs(limit: Int = 50): Flow<List<DetectionEvent>> {
        return dao.getRecentLogs(limit)
    }

    // イベント保存
    suspend fun saveEvent(event: DetectionEvent) {
        dao.insert(event)
    }

    // ログ削除
    suspend fun clearLogs() {
        dao.clearAll()
    }
}

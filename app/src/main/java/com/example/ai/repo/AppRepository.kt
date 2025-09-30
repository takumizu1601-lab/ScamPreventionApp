package com.example.ai.repo

import com.example.ai.ScamApp
import com.example.ai.data.DetectionEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository層
 * - ViewModelから呼び出される窓口
 * - DAOの呼び出しや件数制御を管理
 */
class AppRepository {

    private val dao = ScamApp.db.detectionDao()

    /**
     * 履歴の取得
     * @param limit 件数制御（無料版/有料版で変化）
     */
    fun getRecentLogs(limit: Int): Flow<List<DetectionEvent>> {
        return dao.getRecentLogs(limit)
    }

    /**
     * 検知イベントを保存
     */
    suspend fun saveEvent(event: DetectionEvent) {
        dao.insert(event)
    }

    /**
     * 履歴をすべて削除
     */
    suspend fun clearLogs() {
        dao.clearAll()
    }
}

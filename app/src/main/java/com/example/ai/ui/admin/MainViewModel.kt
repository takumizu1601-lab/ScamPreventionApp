package com.example.ai.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.data.DetectionEvent
import com.example.ai.repo.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class UiState(
    val lastResult: DetectionEvent? = null,
    val history: List<DetectionEvent> = emptyList()
)

class MainViewModel(
    private val repo: AppRepository = AppRepository() // Repository注入（暫定）
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    // 履歴FlowをstateInでキャッシュ（多重collect防止）
    private val logsFlow: StateFlow<List<DetectionEvent>> =
        repo.getRecentLogs(50) // 上限50件（後で無料/有料で切替）
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        // 履歴Flowを監視してUI更新
        viewModelScope.launch {
            logsFlow.collect { logs ->
                _ui.value = _ui.value.copy(history = logs)
            }
        }
    }

    /**
     * 入力テキストを解析して保存
     */
    fun analyzeAndSave(input: String, source: String = "Manual") {
        viewModelScope.launch {
            // 仮の検知ロジック（ランダムスコア）
            val score = Random.nextInt(0, 100)

            val event = DetectionEvent(
                text = input,
                score = score,
                source = source
            )

            // DBに保存
            repo.saveEvent(event)

            // UIのlastResultを更新
            _ui.value = _ui.value.copy(lastResult = event)
        }
    }

    /**
     * 履歴をすべて削除
     */
    fun clearHistory() {
        viewModelScope.launch {
            repo.clearLogs()
            _ui.value = UiState()
        }
    }
}

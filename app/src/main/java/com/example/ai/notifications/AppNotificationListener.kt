package com.example.ai.notifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.ai.risk.Detector
import com.example.ai.notify.WarnNotifier

class AppNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        // 通知リスナーが有効化されたとき
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = (extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "").trim()
        val text = (extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "").trim()
        val bigText = (extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: "").trim()

        val content = listOf(title, text, bigText).filter { it.isNotEmpty() }.joinToString("\n")
        if (content.isEmpty()) return

        // 簡易検知ロジックへ
        val result = Detector.evaluate(content)
        if (result.score >= Detector.DEFAULT_THRESHOLD) {
            WarnNotifier.show(
                context = this,
                sourcePackage = sbn.packageName,
                title = title.ifEmpty { "詐欺疑いの可能性" },
                preview = content.take(120),
                score = result.score,
                hits = result.hits,
                isHighRisk = result.isHighRisk
            )
        }
    }
}
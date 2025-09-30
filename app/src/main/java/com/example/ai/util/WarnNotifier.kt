package com.example.ai.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ai.R

object WarnNotifier {
    private const val CHANNEL_ID = "scam_alerts"

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_scam_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_scam_alerts_desc)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun show(
        context: Context,
        sourcePackage: String,
        title: String,
        preview: String,
        score: Int,
        hits: List<String>
    ) {
        ensureChannel(context)
        val text = "疑い度: $score / 元アプリ: $sourcePackage\n$preview"

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_warning) // ステータスバー用（白アイコン）
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_warning_yellow // 本文用の大アイコン（黄色）
                )
            )
            .setContentTitle("⚠ 詐欺の可能性: $title")
            .setContentText(preview)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            notif
        )
    }
}

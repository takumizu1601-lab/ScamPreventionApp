package com.example.ai.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ai.R

object WarnNotifier {
    private const val CHANNEL_ID = "fraud_warning_channel"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_scam_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_scam_alerts_desc)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun show(
        context: Context,
        sourcePackage: String,
        title: String,
        preview: String,
        score: Int,
        hits: List<String>,
        isHighRisk: Boolean
    ) {
        val displayTitle = if (isHighRisk) "[高リスク] $title" else title

        val contentText = buildString {
            append(preview)
            append("\nスコア: $score")
            if (hits.isNotEmpty()) {
                append("\n検知ワード: ${hits.joinToString(", ")}")
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_warning)
            .setContentTitle(displayTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}

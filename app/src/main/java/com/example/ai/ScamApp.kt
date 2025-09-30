package com.example.ai

import android.app.Application
import androidx.room.Room
import com.example.ai.data.AppDatabase

class ScamApp : Application() {

    companion object {
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        // Roomデータベース初期化
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "scam_app_db"
        )
            .fallbackToDestructiveMigration() // スキーマ変更時はデータ削除（MVP向け）
            .build()
    }
}

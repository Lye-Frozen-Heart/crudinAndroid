package com.lyescorp.crudinandroid

import android.app.Application
import androidx.room.Room
import com.lyescorp.crudinandroid.room.MyDb


class App: Application() {
    lateinit var db: MyDb

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            this,
            MyDb::class.java,
            "article-db"
        ).build()
    }
}
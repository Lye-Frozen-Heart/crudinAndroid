package com.lyescorp.crudinandroid.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Article::class], version = 1)
abstract class MyDb : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}


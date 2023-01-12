package com.lyescorp.crudinandroid.room

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "articles")
 data class Article(
    @PrimaryKey(autoGenerate = false) var codi: String,
    var descri: String,
    var family: Family?,
    var price:Float,
    var stock:Float,
) : java.io.Serializable

enum class Family {
    SOFTWARE,HARDWARE,ALTRES,UNDEFINED
}
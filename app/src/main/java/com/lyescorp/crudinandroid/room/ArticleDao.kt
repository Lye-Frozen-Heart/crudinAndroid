package com.lyescorp.crudinandroid.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM articles ORDER BY codi ASC")
    fun getArticles(): MutableList<Article>

    @Query("SELECT * FROM articles WHERE codi = :codi")
    fun getArticle(codi: String): Article

    @Query("SELECT * FROM articles WHERE codi = :codi")
    fun getArticleExistence(codi: String): Boolean

    @Query("SELECT * FROM articles WHERE stock <= 0")
    fun getNegativeStockArticles():MutableList<Article>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(article: Article):Long

    @Delete
    fun delete(article: Article)

    @Update
    fun update(article: Article)

    @Query("UPDATE articles SET codi =:codi, descri =:descri,family = :family, price = :price, stock =:stock where codi = :codi")
    fun updateWhereCode(codi:String,descri:String,family: Family?,price:Float,stock:Float):Int

    @Query("DELETE FROM articles")
     fun deleteAll()

    @Query("DELETE FROM articles WHERE codi = :codi")
    fun deleteArticleFromCode(codi: String):Int

    @Query("select coalesce(max(codi),0) from articles")
     fun getLastId(): Int
}


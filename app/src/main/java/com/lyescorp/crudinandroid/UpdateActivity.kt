package com.lyescorp.crudinandroid


import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.crudinandroid.room.Article
import com.lyescorp.crudinandroid.room.Family
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.reflect.KClass

class UpdateActivity : AppCompatActivity() {
    lateinit var code:EditText
    lateinit var famSp:Spinner
    lateinit var price:EditText
    lateinit var stock:EditText
    lateinit var descr:EditText
    var articlelist: MutableList<Article>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.setTitle(R.string.edtarticle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //Btnes setOnClickers
        var btnUpdate = findViewById<Button>(R.id.btn_Save)
        var btnDismiss = findViewById<Button>(R.id.btn_Dismiss)

        //Declare textviews and spinner
        code = findViewById(R.id.et_Codi)
        famSp = findViewById(R.id.s_fam)
        price = findViewById(R.id.et_Price)
        stock = findViewById(R.id.et_Stock)
        descr = findViewById(R.id.et_Descr)
        //Do not enable pk and stock to be changed
        code.isEnabled = false
        stock.isEnabled = false

        //Change the text because the add activity its too much similar but different functionalities
        btnDismiss.setText(R.string.delete)
        btnUpdate.setText(R.string.update)
        //Family Spinner adapter part
        val families = Family::class.enumConstantNames()
        val arradapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            families
        )
        arradapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        famSp.adapter = arradapter

        //Get the article pls!
        var articletoupdate = intent.getSerializableExtra("articleupdate") as? Article

        //Set the properties for our textviews and spinner
        code.setText(articletoupdate?.codi);
        famSp.setSelection(arradapter.getPosition(articletoupdate?.family.toString()))
        price.setText(articletoupdate?.price.toString())
        stock.setText(articletoupdate?.stock.toString())
        descr.setText(articletoupdate?.descri.toString())

        btnUpdate.setOnClickListener{
             btnUpdate(it)
            showArticles()
        }
        btnDismiss.setOnClickListener{
                btnCancel(articletoupdate)
                showArticles()
        }
    }
    //Panic FUNCTION
    private fun showArticles() {
        GlobalScope.launch (Dispatchers.IO) {
            articlelist = articleDao.getArticles()
            mAdapter.articulos = articlelist as MutableList<Article>
            withContext(Dispatchers.Main){
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    fun KClass<out Enum<*>>.enumConstantNames() = this.java.enumConstants.map(Enum<*>::name)
    //Go b
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Btn delete
    fun btnCancel(article: Article?) {
        var alertDialogBuilder = AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.delete_by)

        alertDialogBuilder.setPositiveButton(R.string.yes) { dialog, which ->
            GlobalScope.launch (Dispatchers.IO ) {
             articleDao.deleteArticleFromCode(article?.codi.toString())
         }
         showArticles()
         finish() // Do something when user clicks OK
        }
        alertDialogBuilder.setNegativeButton(R.string.no) { dialog, which -> dialog.cancel() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //Btn Update
    fun btnUpdate(view:View){
        var article = Article("N/A","N/A",Family.ALTRES,0F,0F);

        if(code.text.isEmpty()) Snackbar.make(view,R.string.code_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article.codi = code.text.toString()

        when(famSp?.selectedItem){
            "SOFTWARE" -> article?.family = Family.SOFTWARE;
            "HARDWARE" -> article?.family = Family.HARDWARE;
            "ALTRES" -> article?.family = Family.ALTRES;
            "UNDEFINED" -> article?.family = Family.UNDEFINED;
        }

        if(price.text.isEmpty()) Snackbar.make(view,R.string.price_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.price = price?.text.toString().toFloat()

        if(stock.text.isEmpty()) Snackbar.make(view,R.string.stock_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.stock = stock?.text.toString().toFloat()

        if(descr.text.isEmpty()) Snackbar.make(view,R.string.descri_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.descri = descr?.text.toString()

        if(article.codi != "N/A" && article.descri != "N/A" && (article.price >= 0F && article.stock >= 0F)){
            GlobalScope.launch (Dispatchers.IO ) {
                articleDao.updateWhereCode(article.codi,article.descri,article.family,article.price,article.stock)
            }
            finish()
        }else{
            if( code.text.isEmpty()) Snackbar.make(view,R.string.code_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            else if( price.text.isEmpty()) Snackbar.make(view,R.string.price_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            else if( stock.text.isEmpty())Snackbar.make(view,R.string.fieldnotfull, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            else if( descr.text.isEmpty()) Snackbar.make(view,R.string.fieldnotfull, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            else Snackbar.make(view,R.string.fieldnotfull, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        }

    }
}
package com.lyescorp.crudinandroid

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.crudinandroid.room.Article
import com.lyescorp.crudinandroid.room.Family
import java.util.*
import kotlin.reflect.KClass

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.title = getString(R.string.addarticle)

        val families = Family::class.enumConstantNames()
        val famSp = findViewById<Spinner>(R.id.s_fam)
        val arradapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            families
        )
        arradapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        famSp.adapter = arradapter
    }


    fun KClass<out Enum<*>>.enumConstantNames() = this.java.enumConstants.map(Enum<*>::name)

    fun btnCancel(view: View) {
        // cerramos indicando que se cierra con cancel, no hace falta crear el bundle de retorno
        setResult(RESULT_CANCELED)
        finish()
    }

    fun btnAdd(view:View){
        val intent = Intent(this,MainActivity::class.java)
        var article = Article("N/A","N/A",Family.ALTRES,0F,0F);
        var code = findViewById<EditText?>(R.id.et_Codi)
        var fam = findViewById<Spinner?>(R.id.s_fam)
        var price = findViewById<EditText?>(R.id.et_Price)
        var stock = findViewById<EditText?>(R.id.et_Stock)
        var descr = findViewById<EditText?>(R.id.et_Descr)

        if( code.text.isEmpty()) Snackbar.make(view,R.string.code_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article.codi = code.text.toString()

        when( fam?.selectedItem){
            "SOFTWARE" -> article?.family = Family.SOFTWARE;
            "HARDWARE" -> article?.family = Family.HARDWARE;
            "ALTRES" -> article?.family = Family.ALTRES;
            "UNDEFINED" -> article?.family = Family.UNDEFINED;
        }

        if( price.text.isEmpty()) Snackbar.make(view,R.string.price_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.price = price?.text.toString().toFloat()

        if( stock.text.isEmpty())Snackbar.make(view,R.string.stock_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.stock = stock?.text.toString().toFloat()

        if( descr.text.isEmpty()) Snackbar.make(view,R.string.descri_not_f, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        else article?.descri = descr?.text.toString()

        if( article.codi != "N/A" && article.descri != "N/A" && price.text.isNotEmpty() && stock.text.isNotEmpty()){
            intent.putExtra("article", article )
            setResult(Activity.RESULT_OK,intent)
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
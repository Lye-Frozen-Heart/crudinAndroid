package com.lyescorp.crudinandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import com.lyescorp.crudinandroid.adapters.cArticleAdapter
import com.lyescorp.crudinandroid.room.Article
import com.lyescorp.crudinandroid.room.ArticleDao
import kotlinx.coroutines.*

lateinit var articleDao: ArticleDao
private lateinit var parentLayout: View
lateinit var myRecyclerView : RecyclerView

private lateinit var searchView: SearchView;
val mAdapter : cArticleAdapter  = cArticleAdapter()
var articlelist: MutableList<Article>? = null
class MainActivity : AppCompatActivity() {

    lateinit var menu : Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleDao = (applicationContext as App).db.articleDao()
        parentLayout = findViewById(android.R.id.content)
        supportActionBar?.setTitle(R.string.articlist)
        setContentView(R.layout.activity_main)
        setUpRecyclerView(articleDao)
        showArticles()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu

        searchView = menu.findItem(R.id.srch_descri).actionView as SearchView
        var filterdlst:MutableList<Article>;
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                GlobalScope.launch(Dispatchers.IO){
                    if(p0?.isEmpty() == true){
                        showArticles()
                    }else{
                        filterdlst = articleDao.getDescriptionFilteredArticles(p0)
                        mAdapter.articulos = filterdlst
                        mAdapter.notifyDataSetChanged()
                    }

                }

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                    if(p0?.isEmpty() == true){
                        showArticles()
                    }else{
                        GlobalScope.launch(Dispatchers.IO){
                            filterdlst = articleDao.getDescriptionFilteredArticles(p0)
                            mAdapter.articulos = filterdlst
                        }

                        mAdapter.notifyDataSetChanged()
                    }
                return true
            }
        });
        return true
    }







    private fun showArticles() {
        GlobalScope.launch (Dispatchers.IO) {
           articlelist = articleDao.getArticles()
           mAdapter.articulos = articlelist as MutableList<Article>
           withContext(Dispatchers.Main){
               mAdapter.notifyDataSetChanged()
           }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var stockFilter:MenuItem = menu.findItem(R.id.chbx_nstck)
        var articlefilteredlist:MutableList<Article>
        //var descriptionFilter:SearchView = menu.findItem(R.id.srch_descri) as SearchView;

        stockFilter.isChecked = !stockFilter.isChecked

        if(stockFilter.isChecked ){
        GlobalScope.launch(Dispatchers.IO){
            articlefilteredlist = articleDao.getNegativeStockArticles()
            mAdapter.articulos = articlefilteredlist
            mAdapter.notifyDataSetChanged()
         }
        }
        else{
            showArticles()
        }



        return super.onOptionsItemSelected(item)
    }


    fun addArticle(view: View) {
        startFormActivity()
    }


    fun setUpRecyclerView(articleDao:ArticleDao){
        //Cp declare variable up!
        var context = this
        GlobalScope.launch (Dispatchers.IO ) {
            myRecyclerView = findViewById(R.id.rcyvwArticles)
            myRecyclerView.setHasFixedSize(true)
            withContext(Dispatchers.Main){
                myRecyclerView.layoutManager = LinearLayoutManager(context)
            }

            mAdapter.articuloBasicAdapter(articleDao.getArticles(), context)
            withContext(Dispatchers.Main){
                myRecyclerView.adapter = mAdapter
            }

        }
    }

    private fun startFormActivity() {
        val intent = Intent(this, AddActivity::class.java)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val dt = result.data?.getSerializableExtra("article") as? Article

            // ponemos el valor que nos envian en la caja de edición.
            GlobalScope.launch {
                // Construimos el objeto
                // Insertamos el objeto
                if (dt != null) {
                    if(articleDao.getArticleExistence(dt.codi)){
                        Snackbar.make(parentLayout,R.string.artexists, Snackbar.LENGTH_SHORT).setBackgroundTint(
                            Color.YELLOW)
                            .show()
                    }else{
                        articleDao.insert(dt)
                        Snackbar.make(parentLayout,R.string.artcreated, Snackbar.LENGTH_SHORT).setBackgroundTint(
                            Color.BLUE)
                            .show()
                        showArticles()
                    }
                }else{
                    Snackbar.make(parentLayout,R.string.artnotcreated, Snackbar.LENGTH_SHORT).setBackgroundTint(
                        Color.RED)
                        .show()
                    showArticles()
                }
            }
        }
        else {
            Snackbar.make( findViewById<View>(android.R.id.content),
                "The activity has been cancelled",
                Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED)
                .show()
        }
    }


}
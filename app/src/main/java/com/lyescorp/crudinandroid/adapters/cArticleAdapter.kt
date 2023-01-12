package com.lyescorp.crudinandroid.adapters

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.lyescorp.crudinandroid.room.Article
import android.content.Intent
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lyescorp.crudinandroid.*
import com.lyescorp.crudinandroid.room.ArticleDao
import com.lyescorp.crudinandroid.room.Family
import com.lyescorp.crudinandroid.room.MyDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class cArticleAdapter: RecyclerView.Adapter<cArticleAdapter.ViewHolder>() {
    var articulos: MutableList<Article> = ArrayList()
    lateinit var context: Context


    fun articuloBasicAdapter(articulos: MutableList<Article>, context: Context) {
        this.articulos = articulos
        this.context = context
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos.get(position)
        holder.bind(item, context)
    }

    fun updateRecyclerView(articuloslist:MutableList<Article>?){
        if (articuloslist != null) {
            this.articulos = articuloslist
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.recycler_basic_card, parent, false))

    }
    override fun getItemCount(): Int {
        return articulos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var codi = view.findViewById<TextView>(R.id.txtvwCodi)
            var preu = view.findViewById<TextView>(R.id.txtvwPreu)
            var preuiv = view.findViewById<TextView>(R.id.txtvwPrIva)
            var descr = view.findViewById<TextView>(R.id.txtvwDescrip)
            var stock = view.findViewById<TextView>(R.id.txtvwStock)
            var fam = view.findViewById<TextView>(R.id.txtvwFamily)
            var card = view.findViewById<CardView>(R.id.cardRecycler);
            var imvdelete = view.findViewById<ImageView>(R.id.imv_Delete);

        fun bind(articulo: Article, context: Context) {
                codi.text = articulo.codi
                preu.text = articulo.price.toString()
                var iv = articulo.price * 1.21
                preuiv.text = roundToTwoDecimalsString(iv)
                descr.text = articulo.descri
                stock.text = articulo.stock.toString()
                var intentEdit = Intent(context, UpdateActivity::class.java)
                when(articulo.family){
                    Family.SOFTWARE -> fam.text = "SOFTWARE"
                    Family.ALTRES -> fam.text = "ALTRES"
                    Family.HARDWARE -> fam.text = "HARDWARE"
                    else -> fam.text = "N/A"
                }
                card.setOnClickListener{
                    intentEdit.putExtra("articleupdate",articulo);
                    startActivity(context,intentEdit,null)
                }

                imvdelete.setOnClickListener{

                    var alertDialogBuilder = AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(R.string.delete_by)

                    alertDialogBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                        GlobalScope.launch (Dispatchers.IO) {
                            articleDao.deleteArticleFromCode(articulo.codi)
                        }

                        var pos = mAdapter.articulos.indexOf(articulo)
                        mAdapter.articulos.remove(articulo);
                        mAdapter.notifyItemRemoved(pos);
                        mAdapter.notifyItemRangeChanged(pos, mAdapter.articulos.size)
                    }
                    alertDialogBuilder.setNegativeButton(R.string.no) { dialog, which ->
                        dialog.cancel()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()




                }



        }
        fun roundToTwoDecimalsString(number: Double): String {
            return "%.2f".format(number)
        }



    }
}
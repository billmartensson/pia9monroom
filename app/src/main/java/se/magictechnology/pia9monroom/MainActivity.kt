package se.magictechnology.pia9monroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    var catadapter = CategoryAdapter()

    lateinit var databasStuff : ProductDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databasStuff = ProductDB(this)

        catadapter.databasStuff = databasStuff

        var catrv = findViewById<RecyclerView>(R.id.categoriesRV)

        catrv.layoutManager = LinearLayoutManager(this)
        catrv.adapter = catadapter



        findViewById<Button>(R.id.categoryAddButton).setOnClickListener {

            var categoryName = findViewById<EditText>(R.id.categoryNameEdittext).text.toString()

            databasStuff.createCategory(categoryName)

        }

        databasStuff.categories.observe(this, { allCats ->
            /*
            for(cat in allCats)
            {
                Log.d("pia9debug", cat.categoryName!!)
            }
             */
            catadapter.categories = allCats
            catadapter.notifyDataSetChanged()
        })

        databasStuff.getCategories()



        /*

        var testkategori = ProductDB.Category(categoryName = "Möbler")

        launch(Dispatchers.IO) {
            /*
            var testprodukt1 = Product(productName = "Äpple", productPrice = 78, catid = 1)
            db.productDao().insertAll(testprodukt1)
            var testprodukt2 = Product(productName = "Stol", productPrice = 98, catid = 2)
            db.productDao().insertAll(testprodukt2)
            var testprodukt3 = Product(productName = "Banan", productPrice = 5, catid = 1)
            db.productDao().insertAll(testprodukt3)
            var testprodukt4 = Product(productName = "Hylla", productPrice = 73500, catid = 2)
            db.productDao().insertAll(testprodukt4)
            */
            //db.categoryDao().insertAll(testkategori)

            var alltheproducts = databasStuff.db.productDao().getCat(2)

            var productsText = ""
            for(prod in alltheproducts)
            {
                Log.d("pia9debug", prod.productName!! + " " + prod.productPrice.toString())
                Log.d("pia9debug", prod.getPrettyInfo())
                //productsText = productsText + prod.productName!! + " " + prod.productPrice.toString() + "\n"

                productsText = productsText + prod.getPrettyInfo()

            }

            launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.productsTextview).text = productsText
            }

        }
        */
    }




}



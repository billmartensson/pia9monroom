package se.magictechnology.pia9monroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "pia9product"
        ).build()

        var testprodukt = Product(productName = "Äpple", productPrice = 78)

        var testkategori = Category(categoryName = "Frukter")

        launch(Dispatchers.IO) {
            //db.productDao().insertAll(testprodukt)

            db.categoryDao().insertAll(testkategori)

            var alltheproducts = db.productDao().getExpensive(5)

            var productsText = ""
            for(prod in alltheproducts)
            {
                Log.d("pia9debug", prod.productName!! + " " + prod.productPrice.toString())

                productsText = productsText + prod.productName!! + " " + prod.productPrice.toString() + "\n"

            }

            launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.productsTextview).text = productsText
            }

        }

    }
}

@Entity
data class Product(
        @PrimaryKey(autoGenerate = true) val pid: Int = 0,
        @ColumnInfo(name = "product_name") val productName: String?,
        @ColumnInfo(name = "product_price") val productPrice: Int?,
        val catid : Int?
)

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE product_price > :price")
    fun getExpensive(price : Int): List<Product>

    @Query("SELECT * FROM product WHERE catid > :cid")
    fun getCat(cid : Int): List<Product>


    @Insert
    fun insertAll(product: Product)

    @Delete
    fun delete(product: Product)
}

@Entity
data class Category(
        @PrimaryKey(autoGenerate = true) val pid: Int = 0,
        @ColumnInfo(name = "category_name") val categoryName: String?
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Insert
    fun insertAll(category: Category)

    @Delete
    fun delete(category: Category)
}

@Database(entities = arrayOf(Product::class,Category::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}


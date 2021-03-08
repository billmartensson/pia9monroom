package se.magictechnology.pia9monroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE product ADD COLUMN catid INTEGER")
            }
        }

        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "pia9product"
        ).addMigrations(MIGRATION_2_3).build()


        var testkategori = Category(categoryName = "Möbler")

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

            var alltheproducts = db.productDao().getCat(2)

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

    }
}

@Entity
data class Product(
        @PrimaryKey(autoGenerate = true) val pid: Int = 0,
        @ColumnInfo(name = "product_name") val productName: String?,
        @ColumnInfo(name = "product_price") val productPrice: Int?,
        val catid : Int?
) {

    fun getPrettyInfo() : String
    {
        return productName!! + " " + productPrice.toString()
    }
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE product_price > :price")
    fun getExpensive(price : Int): List<Product>

    @Query("SELECT * FROM product WHERE catid = :cid")
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

@Database(entities = arrayOf(Product::class,Category::class), version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}


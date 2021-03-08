package se.magictechnology.pia9monroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        var testprodukt = Product(productName = "Banan", productPrice = 8)

        launch(Dispatchers.IO) {
            db.productDao().insertAll(testprodukt)
        }

        var alltheproducts = db.productDao().getAll()

        for(prod in alltheproducts)
        {
            Log.d("pia9debug", prod.productName!!)
        }
    }
}

@Entity
data class Product(
        @PrimaryKey(autoGenerate = true) val pid: Int = 0,
        @ColumnInfo(name = "product_name") val productName: String?,
        @ColumnInfo(name = "product_price") val productPrice: Int?
)

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Insert
    fun insertAll(product: Product)

    @Delete
    fun delete(product: Product)
}

@Database(entities = arrayOf(Product::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}


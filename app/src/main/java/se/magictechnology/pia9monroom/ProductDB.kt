package se.magictechnology.pia9monroom

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProductDB(ctx : Context) : CoroutineScope by MainScope()  {

    lateinit var db : AppDatabase

    val categories = MutableLiveData<List<Category>>()

    init {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE product ADD COLUMN catid INTEGER")
            }
        }

        db = Room.databaseBuilder(
            ctx,
            AppDatabase::class.java, "pia9product"
        ).addMigrations(MIGRATION_2_3).build()
    }

    fun createCategory(categoryName : String)
    {
        launch(Dispatchers.IO) {
            var testkategori = ProductDB.Category(categoryName = categoryName)
            db.categoryDao().insertAll(testkategori)

            getCategories()
        }
    }

    fun getCategories()
    {
        launch(Dispatchers.IO) {
            val cats = db.categoryDao().getAll()
            launch(Dispatchers.Main) {
                categories.value = cats
            }
        }

    }

    fun deleteCategory(deletecat : Category)
    {
        launch(Dispatchers.IO) {
            db.categoryDao().delete(deletecat)

            getCategories()
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



}
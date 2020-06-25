package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.Product
import com.drugstore.domain.entity.ProductOne
import com.drugstore.domain.entity.ProductTwo
import com.drugstore.domain.entity.ProductWithChildren
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductsDao {

    @Query("SELECT * FROM Products WHERE category_id = :categoryId")
    abstract fun getByCategory(categoryId: Int): Flow<List<Product>>

    @Transaction @Query("SELECT * FROM Products WHERE id = :id")
    abstract fun getWithChildren(id: Int): Flow<ProductWithChildren?>

    @Transaction
    open suspend fun deleteAndInsert(
        categoryId: Int,
        products: List<Product>,
        productOnes: List<ProductOne>,
        productTwos: List<ProductTwo>
    ) {
        delete(categoryId)
        insert(products)
        insertProductOnes(productOnes)
        insertProductTwos(productTwos)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertProductOnes(productOnes: List<ProductOne>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertProductTwos(productTwos: List<ProductTwo>)

    @Query("DELETE FROM Products WHERE category_id = :categoryId")
    protected abstract suspend fun delete(categoryId: Int)
}
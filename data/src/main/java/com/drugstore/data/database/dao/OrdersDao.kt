package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.Order
import com.drugstore.domain.entity.OrderProduct
import com.drugstore.domain.entity.OrderWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrdersDao {

    @Transaction @Query("SELECT * FROM Orders WHERE user_id = ($SELECT_ID_FROM_USER)")
    abstract fun getWithProducts(): Flow<List<OrderWithProducts>>

    @Transaction
    open suspend fun deleteAndInsert(orders: List<Order>, orderProducts: List<OrderProduct>) {
        delete()
        insert(orders)
        insertOrderProducts(orderProducts)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(products: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertOrderProducts(orderProducts: List<OrderProduct>)

    @Query("DELETE FROM Orders WHERE user_id = ($SELECT_ID_FROM_USER)")
    abstract suspend fun delete()
}
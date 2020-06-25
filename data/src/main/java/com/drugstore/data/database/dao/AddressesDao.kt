package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.Address
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AddressesDao {

    @Query("SELECT * FROM Addresses WHERE user_id = ($SELECT_ID_FROM_USER)")
    abstract fun get(): Flow<List<Address>>

    @Query("SELECT * FROM Addresses WHERE id = :id AND user_id = ($SELECT_ID_FROM_USER)")
    abstract fun get(id: Int): Flow<Address?>

    @Transaction
    open suspend fun deleteAndInsert(addresses: List<Address>) {
        delete()
        insert(addresses)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(addresses: List<Address>)

    @Query("DELETE FROM Addresses WHERE user_id = ($SELECT_ID_FROM_USER)")
    abstract suspend fun delete()
}
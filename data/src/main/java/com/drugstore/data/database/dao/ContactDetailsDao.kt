package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.ContactDetail
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ContactDetailsDao {

    @Query("SELECT * FROM ContactDetails")
    abstract fun get(): Flow<List<ContactDetail>>

    @Transaction
    open suspend fun deleteAndInsert(contactDetails: List<ContactDetail>) {
        delete()
        insert(contactDetails)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(contactDetails: List<ContactDetail>)

    @Query("DELETE FROM ContactDetails")
    abstract suspend fun delete()
}
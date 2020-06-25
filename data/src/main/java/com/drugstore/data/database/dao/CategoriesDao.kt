package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoriesDao {

    @Query("SELECT * FROM Categories")
    abstract fun get(): Flow<List<Category>>

    @Query("SELECT children_ids FROM Categories WHERE id = :id")
    abstract suspend fun getChildrenIdsSync(id: Int): String?

    @Transaction
    open suspend fun deleteAndInsert(categories: List<Category>) {
        delete()
        insert(categories)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(categories: List<Category>)

    @Query("DELETE FROM Categories")
    abstract suspend fun delete()
}
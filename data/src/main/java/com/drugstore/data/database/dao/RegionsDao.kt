package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.domain.entity.Region
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RegionsDao {

    @Query("SELECT * FROM Regions")
    abstract fun getAll(): Flow<List<Region>>

    @Query("SELECT country_id, country_name FROM Regions")
    abstract suspend fun getCountriesSuspend(): List<Region.Country>

    @Query("SELECT name FROM Regions WHERE id = :id")
    abstract fun getName(id: String): Flow<String?>

    @Query("SELECT country_id, country_name FROM Regions WHERE id = :id")
    abstract fun getCountry(id: String): Flow<Region.Country?>

    @Transaction
    open suspend fun deleteAndInsert(regions: List<Region>) {
        delete()
        insert(regions)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(regions: List<Region>)

    @Query("DELETE FROM Regions")
    protected abstract suspend fun delete()
}
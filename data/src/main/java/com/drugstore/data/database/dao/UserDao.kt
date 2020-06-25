package com.drugstore.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drugstore.domain.entity.User
import kotlinx.coroutines.flow.Flow

internal const val SELECT_ID_FROM_USER = "SELECT id FROM User"

@Dao
abstract class UserDao {

    @Query("SELECT * FROM User")
    abstract fun getUser(): Flow<User?>

    @Query(SELECT_ID_FROM_USER)
    abstract suspend fun getIdSuspend(): Int

    @Query("SELECT email FROM User")
    abstract fun getEmail(): Flow<String?>

    @Query("SELECT email FROM User")
    abstract suspend fun getEmailSuspend(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(user: User)

    @Query("DELETE FROM User")
    abstract suspend fun delete()
}
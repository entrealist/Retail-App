package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.data.database.entity.BiometricCredentials
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BiometricCredentialsDao {

    @Query("SELECT email FROM BiometricCredentials")
    abstract fun getEmail(): Flow<String?>

    @Query("SELECT encoded_password FROM BiometricCredentials")
    abstract suspend fun getEncodedPasswordSuspend(): String?

    @Transaction
    open suspend fun deleteAndInsert(email: String, encodedPassword: String) {
        delete()
        insert(
            BiometricCredentials(
                email,
                encodedPassword
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(biometricCredentials: BiometricCredentials)

    @Query("DELETE FROM BiometricCredentials")
    abstract suspend fun delete()
}
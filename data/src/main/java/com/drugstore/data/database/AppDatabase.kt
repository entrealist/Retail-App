package com.drugstore.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drugstore.data.BuildConfig
import com.drugstore.data.database.dao.*
import com.drugstore.data.database.entity.BiometricCredentials
import com.drugstore.data.database.entity.CacheRecord
import com.drugstore.data.database.entity.GeneralInfo
import com.drugstore.data.database.entity.Session
import com.drugstore.domain.entity.*

@Database(
    entities = [
        Address::class,
        BiometricCredentials::class,
        CacheRecord::class,
        Category::class,
        ContactDetail::class,
        GeneralInfo::class,
        Order::class,
        OrderProduct::class,
        Product::class,
        ProductOne::class,
        ProductTwo::class,
        Region::class,
        Session::class,
        User::class
    ],
    version = BuildConfig.APP_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun addressesDao(): AddressesDao
    abstract fun biometricCredentialsDao(): BiometricCredentialsDao
    abstract fun cacheRecordsDao(): CacheRecordsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun contactDetailsDao(): ContactDetailsDao
    abstract fun generalInfoDao(): GeneralInfoDao
    abstract fun ordersDao(): OrdersDao
    abstract fun productsDao(): ProductsDao
    abstract fun regionsDao(): RegionsDao
    abstract fun sessionDao(): SessionDao
    abstract fun userDao(): UserDao
}
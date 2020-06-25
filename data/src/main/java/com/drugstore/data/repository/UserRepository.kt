package com.drugstore.data.repository

import com.drugstore.data.database.dao.UserDao
import com.drugstore.domain.entity.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    val user get() = userDao.getUser()
    internal val email get() = userDao.getEmail()
    internal suspend fun getEmail() = userDao.getEmailSuspend()

    internal suspend fun getId() = userDao.getIdSuspend()

    internal suspend fun setUser(user: User) {
        userDao.insert(user)
    }
}
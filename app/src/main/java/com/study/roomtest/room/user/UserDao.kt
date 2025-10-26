package com.study.roomtest.room.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User>

    @Query("SELECT * FROM users ORDER BY firstName ASC")
    fun getAllUsers(): Flow<List<User>>
}
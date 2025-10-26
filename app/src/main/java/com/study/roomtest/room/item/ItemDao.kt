package com.study.roomtest.room.item

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAll(): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
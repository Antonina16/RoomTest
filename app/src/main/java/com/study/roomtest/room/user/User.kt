package com.study.roomtest.room.user

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val phoneNumber: String,
    @Embedded
    val homeAddress: HomeAddress
)

data class HomeAddress(
    val country: String,
    val city: String,
    val street: String,
    val building: String,
    val apartment: String,
    val zipCode: String
)
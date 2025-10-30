package com.study.roomtest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.study.roomtest.room.user.User
import com.study.roomtest.room.user.UserDao
import com.study.roomtest.room.user.UserDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.study.roomtest.viewmodels.UserField.*
import com.study.roomtest.room.*
import com.study.roomtest.room.user.HomeAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class UserState(
    val firstName: String = "",
    val lastName: String = "",
    val emailAddress: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val city: String = "",
    val street: String = "",
    val building: String = "",
    val apartment: String = "",
    val zipCode: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailAddressError: String? = null,
    val phoneNumberError: String? = null,
    val cityError: String? = null
)

enum class UserField {
    FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PHONE_NUMBER, COUNTRY, CITY, STREET, BUILDING, APARTMENT, ZIP_CODE
}

class UserViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val PHONE_NUMBER_PATTERN = Regex("""\+38\(\d{3}\)\d{3}-\d{2}-\d{2}""")
    }

    private val _uiState = MutableStateFlow(UserState())
    val uiState: StateFlow<UserState> = _uiState.asStateFlow()

    private val userDao: UserDao = UserDatabase.getDatabase(application).userDao()

    val hotFlow = userDao.getAllUsers()
        .onStart { println("Hot Flow (source): started collecting from DB") }
        .onEach { println("Hot Flow (source): emitted ${it.size} items") }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val isSaveEnabled: StateFlow<Boolean> =
        uiState
            .map { state ->
                val requiredFilled =
                    state.firstName.isNotBlank() &&
                            state.lastName.isNotBlank() &&
                            state.emailAddress.isNotBlank() &&
                            state.phoneNumber.isNotBlank() &&
                            state.city.isNotBlank()

                val noErrors =
                    state.firstNameError == null &&
                            state.lastNameError == null &&
                            state.emailAddressError == null &&
                            state.phoneNumberError == null &&
                            state.cityError == null

                requiredFilled && noErrors
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                false
            )

    fun onFieldChange(value: String, field: UserField) {
        _uiState.update {
            when (field) {
                FIRST_NAME -> it.copy(firstName = value)
                LAST_NAME -> it.copy(lastName = value)
                EMAIL_ADDRESS -> it.copy(emailAddress = value)
                PHONE_NUMBER -> it.copy(phoneNumber = value)
                COUNTRY -> it.copy(country = value)
                CITY -> it.copy(city = value)
                STREET -> it.copy(street = value)
                BUILDING -> it.copy(building = value)
                APARTMENT -> it.copy(apartment = value)
                ZIP_CODE -> it.copy(zipCode = value)
            }
        }
    }

    fun validateField(field: UserField, value: String): String? {
        val error = when (field) {
            FIRST_NAME -> {
                val error = if (value.isBlank()) "First name cannot be empty" else null
                _uiState.update { item -> item.copy(firstNameError = error) }
                error
            }

            LAST_NAME -> {
                val error = if (value.isBlank()) "Last name cannot be empty" else null
                _uiState.update { item -> item.copy(lastNameError = error) }
                error
            }

            EMAIL_ADDRESS -> {
                val error = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
                    "Invalid email address"
                else null
                _uiState.update { item -> item.copy(emailAddressError = error) }
                error
            }

            PHONE_NUMBER -> {
                val error = if (value.length < 9)
                    "Phone number should be in format +38(066)111-11-11"
                else null
                _uiState.update { item -> item.copy(phoneNumberError = error, phoneNumber = value) }
                error
            }
            CITY -> {
                val error = if (value.isBlank()) "City cannot be empty"
                else null
                _uiState.update { item -> item.copy(cityError = error) }
                error
            }
            else -> null
        }
        return error
    }


    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        println("Inserting '${user.firstName}'")
        userDao.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) { userDao.updateUser(user) }
    fun deleteUser(user: User) = viewModelScope.launch(Dispatchers.IO) { userDao.deleteUser(user) }

    fun saveUser() {
        if (!validate()) return
        val s = _uiState.value
        val user = User(
            firstName = s.firstName,
            lastName = s.lastName,
            emailAddress = s.emailAddress,
            phoneNumber = s.phoneNumber,
            homeAddress = HomeAddress(
                country = s.country,
                city = s.city,
                street = s.street,
                building = s.building,
                apartment = s.apartment,
                zipCode = s.zipCode
            )
        )
        insertUser(user)
        println("devcpp User saved")
    }

    private fun validate(): Boolean {
        var isValid = true
        _uiState.update { item ->
            item.copy(
                firstNameError = if (item.firstName.isBlank()) {
                    isValid = false; "First name cannot be empty."
                } else null,
                lastNameError = if (item.lastName.isBlank()) {
                    isValid = false; "Last name cannot be empty."
                } else null,
                emailAddressError = if (!android.util.Patterns.EMAIL_ADDRESS
                        .matcher(item.emailAddress).matches()
                ) {
                    isValid = false; "Invalid email address."
                } else null,
                phoneNumberError = if (item.phoneNumber.isBlank()) {
                    isValid = false; "Phone number should be in format +38(0__) ___-__-__"
                } else null,
                cityError = if (item.city.isBlank()) {
                    isValid = false; "City cannot be empty."
                } else null,
            )
        }
        return isValid
    }
}

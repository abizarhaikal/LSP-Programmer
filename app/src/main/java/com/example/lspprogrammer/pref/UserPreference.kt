package com.example.lspprogrammer.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>){
    suspend fun saveSession(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = userModel.email
            preferences[ID_KEY] = userModel.userId
            preferences[USER_KEY] = userModel.name
            preferences[IS_LOGIN_KEY] = true
            preferences[ROLE_KEY] =userModel.role
        }
        Log.d("UserPreference", "Session saved: ${userModel.email}, ${userModel.name}")
    }

    fun getSession() : Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[ID_KEY] ?: "",
                preferences[USER_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[ROLE_KEY] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { prefereneces ->
            prefereneces.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val ID_KEY = stringPreferencesKey("userId")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val USER_KEY = stringPreferencesKey("user")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.vincent.givetake.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val USER_KEY = stringPreferencesKey("user_key")
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_POINT = intPreferencesKey("user_point")

    fun getUserAccessKey(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_KEY] ?: ""
        }
    }

    fun getUserPoint(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[USER_POINT] ?: 0
        }
    }

    fun getUserId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID] ?: ""
        }
    }

    suspend fun saveUserAccessKey(key: String) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = key
        }
    }

    suspend fun saveUserPoint(point: Int) {
        dataStore.edit { preferences ->
            preferences[USER_POINT] = point
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }



    companion object {
        @Volatile
        private var INSTANCE : UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
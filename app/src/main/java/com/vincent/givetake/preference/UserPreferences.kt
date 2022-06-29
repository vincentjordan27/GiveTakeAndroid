package com.vincent.givetake.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val USER_KEY = stringPreferencesKey("user_key")
    private val USER_ID = stringPreferencesKey("user_id")
    private val INDEX_FILTER = intPreferencesKey("index_filter")
    private val RADIUS_FILTER= stringPreferencesKey("radius_filter")
    private val NAME_FILTER = stringPreferencesKey("name_filter")

    fun getUserAccessKey(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_KEY] ?: ""
        }
    }

    fun getUserId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID] ?: ""
        }
    }

    fun getFilterData(): Flow<FilterData> {
        return dataStore.data.map { preferences ->
            return@map FilterData(
                preferences[RADIUS_FILTER] ?: "",
                preferences[INDEX_FILTER] ?: -1,
                preferences[NAME_FILTER] ?: ""
            )
        }
    }

    suspend fun saveUserAccessKey(key: String) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = key
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun saveFilterSearch(data: FilterData) {
        dataStore.edit { preference ->
            preference[INDEX_FILTER] = data.index
            preference[NAME_FILTER] = data.category
            preference[RADIUS_FILTER] = data.radius
        }
    }

    suspend fun reset() {
        dataStore.edit {  preference ->
            preference[INDEX_FILTER] = -1
            preference[NAME_FILTER] = ""
            preference[RADIUS_FILTER] = ""
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
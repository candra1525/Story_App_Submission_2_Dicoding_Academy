package com.candra.dicodingstoryapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.candra.dicodingstoryapplication.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class TokenPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun setSessionToken(user: UserModel) {
        dataStore.edit { pref ->
            pref[TOKEN_KEY] = user.token
            pref[NAME_KEY] = user.name
            pref[IS_USER_LOGIN_KEY] = true
        }
    }

    fun getSessionToken(): Flow<UserModel> {
        return dataStore.data.map { pref ->
            UserModel(
                pref[TOKEN_KEY] ?: "",
                pref[NAME_KEY] ?: "",
                pref[IS_USER_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref[TOKEN_KEY] = ""
            pref[NAME_KEY] = ""
            pref[IS_USER_LOGIN_KEY] = false
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: TokenPreferences? = null
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val IS_USER_LOGIN_KEY = booleanPreferencesKey("isUserLogin")

        fun getInstance(dataStore: DataStore<Preferences>): TokenPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
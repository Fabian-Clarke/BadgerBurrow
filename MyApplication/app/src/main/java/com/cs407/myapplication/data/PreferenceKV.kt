package com.cs407.myapplications.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class PreferenceKV(private val context: Context, private val userUID: String) {

    companion object {
        private val Context.dataStore by preferencesDataStore("preferenceKV")
    }

    private val userUIDPre = stringPreferencesKey(userUID)

    @Serializable
    data class AppPreferences(
        var greeting: String = "Welcome",
    )

    val appPreferencesFlow: Flow<AppPreferences> =
        context.dataStore.data.map { preferences ->
            val jsonString = preferences[userUIDPre] ?: """
                {
                  "greeting": "Welcome"
                }
            """.trimIndent()
            Json.decodeFromString<AppPreferences>(jsonString)
        }

    suspend fun saveGreeting(greeting: String) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[userUIDPre] ?: "{}"
            val current: AppPreferences =
                Json.decodeFromString(jsonString.ifBlank { "{}" })
            current.greeting = greeting
            preferences[userUIDPre] = Json.encodeToString(AppPreferences.serializer(), current)
        }
    }
}

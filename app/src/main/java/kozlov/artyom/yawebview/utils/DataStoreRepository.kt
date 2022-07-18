package kozlov.artyom.yawebview.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataStoreRepository.PREFERENCE_NAME)


class DataStoreRepository(val context: Context) {


    suspend fun saveToDataStore(name: String) {
        context.dataStore.edit { preference ->
            preference[NAME] = name
        }
    }

    val readFromDataStore: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            val name = preference[NAME] ?: NONE
            name
        }

    companion object {
        const val PREFERENCE_NAME = "my_preference"
        val NAME = stringPreferencesKey("my_name")
        const val NONE = "https://yandex.com/"
        const val ERROR = "error"
    }
}

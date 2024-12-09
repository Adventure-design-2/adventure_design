import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DDayDataStore {
    companion object {
        private const val DATASTORE_NAME = "dday_prefs"
        private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
        private val DDAY_KEY = stringPreferencesKey("dday_key")
    }

    // D-Day 저장
    suspend fun saveDDay(context: Context, dDay: String) {
        context.dataStore.edit { preferences ->
            preferences[DDAY_KEY] = dDay
        }
    }

    // D-Day 가져오기
    fun getDDayFlow(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[DDAY_KEY]
            }
    }

    // D-Day 동기적으로 가져오기 (필요시 사용)
    suspend fun getDDay(context: Context): String? {
        return context.dataStore.data
            .map { preferences ->
                preferences[DDAY_KEY]
            }
            .firstOrNull()
    }
}

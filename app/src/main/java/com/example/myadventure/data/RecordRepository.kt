import com.example.myadventure.model.Record
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RecordRepository {
    private val database = FirebaseDatabase.getInstance().getReference("records")

    // 기록 추가
    suspend fun addRecord(record: Record): Boolean {
        return try {
            database.child(record.recordId).setValue(record).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    // 기록 삭제
    suspend fun deleteRecord(recordId: String): Boolean {
        return try {
            database.child(recordId).removeValue().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

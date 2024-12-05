package com.example.myadventure.data

import com.example.myadventure.model.Record
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RecordRepository {
    private val database = FirebaseDatabase.getInstance().reference

    // 기록 저장
    suspend fun saveRecord(record: Record) {
        val recordId = record.recordId.ifEmpty { database.child("records").push().key ?: "" }
        if (recordId.isNotEmpty()) {
            database.child("records").child(recordId).setValue(record).await()
        }
    }

    // 기록 로드
    suspend fun loadRecord(recordId: String): Record? {
        val snapshot = database.child("records").child(recordId).get().await()
        return snapshot.getValue(Record::class.java)
    }

    // 파트너와 공유된 기록 가져오기
    suspend fun loadPartnerRecords(partnerUid: String): List<Record> {
        val snapshot = database.child("records").orderByChild("partnerUid").equalTo(partnerUid).get().await()
        return snapshot.children.mapNotNull { it.getValue(Record::class.java) }
    }
}

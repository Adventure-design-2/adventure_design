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

    // 실시간 데이터베이스 변경 감지
    fun listenToPartnerRecords(partnerUid: String, onRecordChanged: (Record) -> Unit) {
        database.child("records").orderByChild("partnerUid").equalTo(partnerUid)
            .addChildEventListener(object : com.google.firebase.database.ChildEventListener {
                override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Record::class.java)?.let(onRecordChanged)
                }

                override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Record::class.java)?.let(onRecordChanged)
                }

                override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) {}
                override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
    }
}

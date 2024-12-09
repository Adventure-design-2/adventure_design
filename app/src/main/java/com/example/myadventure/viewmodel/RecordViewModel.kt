package com.example.myadventure.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myadventure.model.Record
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RecordViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    // Holds the list of records to display
    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> get() = _records

    // Load records from local storage
    suspend fun loadRecordsFromLocal(context: Context) {
        try {
            withContext(Dispatchers.IO) {
                val file = File(context.filesDir, "records.txt")
                if (file.exists()) {
                    val recordsList = mutableListOf<Record>()
                    FileInputStream(file).bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            val record = parseRecordFromString(line)
                            if (record != null) {
                                recordsList.add(record)
                            }
                        }
                    }
                    _records.value = recordsList
                    Log.d("RecordViewModel", "Records loaded: ${recordsList.size}")
                } else {
                    Log.d("RecordViewModel", "No records file found.")
                }
            }
        } catch (e: IOException) {
            Log.e("RecordViewModel", "Error loading records: ${e.message}", e)
        }
    }

    // Parse a record from a single line of text
    private fun parseRecordFromString(line: String): Record? {
        return try {
            val parts = line.split(",")
            if (parts.size == 6) {
                Record(
                    recordId = parts[0],
                    title = parts[1],
                    description = parts[2],
                    image = parts[3],
                    timestamp = parts[5].toLong()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("RecordViewModel", "Error parsing record: ${e.message}", e)
            null
        }
    }

    fun uploadRecord(record: Record, onComplete: (Boolean) -> Unit) {
        val recordRef = FirebaseDatabase.getInstance().getReference("records").push()
        record.recordId = recordRef.key ?: return onComplete(false)

        recordRef.setValue(record).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

}
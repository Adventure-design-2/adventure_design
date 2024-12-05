package com.example.myadventure.data

import android.content.Context
import com.example.myadventure.model.Record
import java.io.File
import java.io.FileOutputStream
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class LocalStorageManager(private val context: Context) {
    fun saveRecordLocally(record: Record) {
        val fileName = "record_${record.recordId}.json"
        val file = File(context.filesDir, fileName)
        val json = Json.encodeToString(record)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
    }
}

package com.example.myadventure.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myadventure.data.LocalStorageManager
import com.example.myadventure.data.RecordRepository
import com.example.myadventure.model.Record
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordViewModel(private val context: Context) : ViewModel() {
    private val repository = RecordRepository()
    private val localStorageManager = LocalStorageManager(context)

    private val _partnerRecord = MutableStateFlow<Record?>(null)
    val partnerRecord: StateFlow<Record?> = _partnerRecord

    // 기록 저장
    fun saveRecord(record: Record) {
        viewModelScope.launch {
            repository.saveRecord(record)
        }
    }

    // 로컬 저장
    fun saveRecordLocally(record: Record) {
        localStorageManager.saveRecordLocally(record)
    }

    // 파트너 기록 감지
    fun listenToPartnerRecords(partnerUid: String) {
        repository.listenToPartnerRecords(partnerUid) { record ->
            _partnerRecord.value = record
        }
    }
}

class RecordViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


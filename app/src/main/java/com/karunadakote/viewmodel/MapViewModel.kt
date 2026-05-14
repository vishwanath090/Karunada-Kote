package com.karunadakote.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karunadakote.data.model.ApiResult
import com.karunadakote.data.model.Fort
import com.karunadakote.data.repository.FortRepository
import kotlinx.coroutines.launch
import java.util.Locale

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FortRepository(application.applicationContext)

    private val _forts = MutableLiveData<List<Fort>>()
    val forts: LiveData<List<Fort>> = _forts

    private val _selectedFort = MutableLiveData<Fort?>()
    val selectedFort: LiveData<Fort?> = _selectedFort

    private val _aiDescription = MutableLiveData<ApiResult<String>?>()
    val aiDescription: LiveData<ApiResult<String>?> = _aiDescription

    private val _visitedIds = MutableLiveData<Set<Int>>()
    val visitedIds: LiveData<Set<Int>> = _visitedIds

    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false

    init {
        loadForts()
        initTts()
    }

    fun loadForts() {
        viewModelScope.launch {
            try {
                _forts.value = repository.loadForts()
                _visitedIds.value = repository.getVisitedFortIds()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectFort(fort: Fort) {
        _selectedFort.value = fort
        _aiDescription.value = null
        repository.markFortVisited(fort.id)
        _visitedIds.value = repository.getVisitedFortIds()
    }

    fun clearSelectedFort() {
        _selectedFort.value = null
        _aiDescription.value = null
    }

    fun generateAiDescription(fortName: String) {
        _aiDescription.value = ApiResult.Loading
        viewModelScope.launch {
            val result = repository.generateAiDescription(fortName)
            _aiDescription.value = result
        }
    }

    fun speakText(text: String) {
        if (ttsReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fort_description")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.ENGLISH
                ttsReady = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

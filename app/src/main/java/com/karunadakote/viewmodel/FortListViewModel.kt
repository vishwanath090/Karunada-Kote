package com.karunadakote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karunadakote.data.model.Fort
import com.karunadakote.data.repository.FortRepository
import kotlinx.coroutines.launch

class FortListViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository =
        FortRepository(application.applicationContext)

    private val _forts = MutableLiveData<List<Fort>>()
    val forts: LiveData<List<Fort>> = _forts

    private val _visitedIds = MutableLiveData<Set<Int>>()
    val visitedIds: LiveData<Set<Int>> = _visitedIds

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadForts()
    }

    fun loadForts() {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val loadedForts = repository.loadForts()

                _forts.value = loadedForts

                _visitedIds.value =
                    repository.getVisitedFortIds()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    "Failed to load forts: ${e.localizedMessage}"

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun refreshVisitedIds() {

        _visitedIds.value =
            repository.getVisitedFortIds()
    }
}
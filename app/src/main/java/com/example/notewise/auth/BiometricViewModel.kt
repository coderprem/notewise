package com.example.notewise.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor() : ViewModel() {
    private val _biometricResult = MutableStateFlow<BiometricPromptManager.BioMetricResult?>(null)
    val biometricResult: StateFlow<BiometricPromptManager.BioMetricResult?> = _biometricResult

    fun setResult(result: BiometricPromptManager.BioMetricResult) {
        _biometricResult.value = result
    }

    fun reset() {
        _biometricResult.value = null
    }
}

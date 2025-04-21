package com.example.notewise.presentation.note

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notewise.data.local.entity.NoteEntity
import com.example.notewise.data.remote.api.HuggingFaceTextApi
import com.example.notewise.data.remote.api.NetworkResponse
import com.example.notewise.data.remote.api.Parameters
import com.example.notewise.data.remote.api.RetrofitTextInstance
import com.example.notewise.data.remote.model.ApiRequest
import com.example.notewise.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    @Named("huggingface_api") private val api: HuggingFaceTextApi // Inject the API
) : ViewModel() {
    val resultState = mutableStateOf<NetworkResponse<List<String>>?>(null)

    val notes = repository.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addNote(title: String?, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            resultState.value = NetworkResponse.Loading
            val categories = classifyNote(content)
            val note = NoteEntity(
                title = title?.takeIf { it.isNotBlank() }, // ⬅️ Removes "Untitled"
                content = content,
                categories = categories,
                timestamp = System.currentTimeMillis()
            )
            repository.insertNote(note)
            onSuccess()
            resultState.value = null // Reset after save
        }
    }

    fun toggleBookmark(note: NoteEntity) {
        viewModelScope.launch {
            repository.insertNote(note.copy(isBookmarked = !note.isBookmarked))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun updateNote(note: NoteEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val updatedCategories = classifyNote(note.content)
            val updatedNote = note.copy(categories = updatedCategories)
            repository.updateNote(updatedNote)
            onSuccess()
        }
    }

    suspend fun classifyNote(text: String): List<String> {
        resultState.value = NetworkResponse.Loading

        // Step 1: Clean the input text to normalize it
        val cleanedText = text.trim().replace("\n", " ").replace(Regex("[ ]+"), " ")

        val request = ApiRequest(
            inputs = cleanedText,
            parameters = Parameters(
                candidate_labels = listOf(
                    "Work", "Personal", "Tech", "Health", "Finance",
                    "Shopping", "Ideas", "Travel",
                ),
                multi_label = true
            )
        )

        return try {
            // Step 2: Detect contact numbers before classification
            val phoneNumberPattern =
                "\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}".toRegex()
            if (phoneNumberPattern.containsMatchIn(cleanedText)) {
                resultState.value = NetworkResponse.Success(listOf("Contacts"))
                return listOf("Contacts")
            }

            val response = RetrofitTextInstance.api.classifyText(request)

            if (response.isSuccessful) {
                val body = response.body()
                val labels = body?.labels.orEmpty()
                val scores = body?.scores.orEmpty()
                val labelScorePairs = labels.zip(scores)

                Log.d("Classifier", "Raw Scores: $labelScorePairs")

                val aboveThreshold = labelScorePairs.filter { it.second > 0.3 }

                val resultCategories = when {
                    aboveThreshold.size == 1 -> listOf(aboveThreshold[0].first) // Exactly one above 0.25
                    aboveThreshold.size > 1 -> aboveThreshold
                        .sortedByDescending { it.second }
                        .take(3)
                        .map { it.first } // Top 3
                    else -> listOf("Ideas") // None above 0.25
                }


                resultState.value = NetworkResponse.Success(resultCategories)
                return resultCategories
            } else {
                resultState.value = NetworkResponse.Error("Error: ${response.message()}")
                return listOf("Ideas")
            }

        } catch (e: Exception) {
            resultState.value = NetworkResponse.Error("Exception: ${e.localizedMessage}")
            return listOf("Ideas")
        }
    }
}

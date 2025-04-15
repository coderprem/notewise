package com.example.notewise.presentation.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notewise.data.remote.api.NetworkResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    noteId: Int? = null,
    onNoteSaved: () -> Unit
) {
    val allNotes by viewModel.notes.collectAsState()
    val noteToEdit = allNotes.find { it.id == noteId }

    var title by rememberSaveable(noteToEdit) { mutableStateOf(noteToEdit?.title.orEmpty()) }
    var content by rememberSaveable(noteToEdit) { mutableStateOf(noteToEdit?.content.orEmpty()) }

    val loadingState = viewModel.resultState.value is NetworkResponse.Loading
    val isEditMode = noteToEdit != null
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isEditMode) "Edit Note" else "Add Note",
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        if (isEditMode) {
                            viewModel.updateNote(
                                noteToEdit.copy(
                                    title = title.ifBlank { null },
                                    content = content,
                                    timestamp = System.currentTimeMillis()
                                )
                            ) {
                                onNoteSaved()
                            }
                        } else {
                            viewModel.addNote(title.ifBlank { null }, content) {
                                onNoteSaved()
                            }
                        }
                    }
                },
                enabled = !loadingState && content.isNotBlank(),
            ) {
                if (loadingState) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 100) title = it },
            label = { Text("Title (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Write your note...") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f) // Takes up to 60% of the screen height (can be adjusted)
                .focusRequester(focusRequester)
                .verticalScroll(rememberScrollState()), // Scrollable content
            textStyle = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp), // Bigger height for each line
        )
    }
}

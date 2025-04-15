package com.example.notewise.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notewise.presentation.note.AddNoteScreen
import com.example.notewise.presentation.note.NoteListScreen
import com.example.notewise.presentation.note.NoteViewModel


@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NoteViewModel
) {
    NavHost(navController = navController, startDestination = Screens.NoteListScreen) {

        composable<Screens.NoteListScreen> {
            NoteListScreen(
                noteViewModel = viewModel,
                onAddNoteClick = {
                    navController.navigate(Screens.AddNoteScreen(null))
                },
                onEditClick = { it ->
                    navController.navigate(Screens.AddNoteScreen(it))
                }
            )
        }

        composable<Screens.AddNoteScreen> { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            AddNoteScreen(
                onNoteSaved = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                noteId = noteId,
                modifier = modifier
            )
        }

    }
}
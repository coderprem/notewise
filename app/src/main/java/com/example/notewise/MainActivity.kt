package com.example.notewise

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.notewise.auth.BiometricPromptManager
import com.example.notewise.auth.BiometricPromptManager.BioMetricResult
import com.example.notewise.presentation.navigation.NavGraph
import com.example.notewise.presentation.note.NoteViewModel
import com.example.notewise.ui.theme.NoteWiseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(
            activity = this,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteWiseTheme {

                val biometricResult by promptManager.promptResults.collectAsState(initial = null)

                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                                Toast.makeText(this@MainActivity, "Enrolled", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Enrollment failed", Toast.LENGTH_SHORT).show()
                            }
                        println(result)
                    }
                )

                LaunchedEffect(Unit) {
                    val biometricManager = BiometricManager.from(this@MainActivity)
                    val authenticators = if (Build.VERSION.SDK_INT >= 30) {
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or DEVICE_CREDENTIAL
                    } else {
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    }

                    when (biometricManager.canAuthenticate(authenticators)) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            promptManager.showBiometricPrompt(
                                title = "Authenticate",
                                description = "Please authenticate to access your notes."
                            )
                        }

                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Biometric enrollment not supported on this device.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Biometric hardware unavailable or not supported.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }


                biometricResult?.let { result ->
                    when (result) {
                        is BioMetricResult.AuthenticationSuccess -> {
                            val navController = rememberNavController()
                            val noteViewModel: NoteViewModel = hiltViewModel()
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                NavGraph(
                                    modifier = Modifier.padding(innerPadding),
                                    navController = navController,
                                    viewModel = noteViewModel
                                )
                            }
                        }
                        is BioMetricResult.HardwareUnavailable,
                        is BioMetricResult.AuthenticationNotSet,
                        is BioMetricResult.FeatureUnavailable -> {
                            Helper("Biometric not available or not set up")
                        }
                        is BioMetricResult.AuthenticationFailed,
                        is BioMetricResult.AuthenticationError -> {
                            RetryScreen(
                                message = (result as? BioMetricResult.AuthenticationError)?.error ?: "Authentication failed",
                                onRetry = {
                                    promptManager.showBiometricPrompt(
                                        title = "Authenticate",
                                        description = "Please authenticate to access your notes."
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RetryScreen(message: String, onRetry: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("RetryScreen", "message: $message")
//            Text(text = message)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Unlock NoteWise")
            }
        }
    }
}


@Composable
fun Helper(text: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text)
        }
    }
}
package com.proofmode.c2pa.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proofmode.c2pa.ui.theme.ProofmodeC2paTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProofmodeC2paTheme {
                ProofAppNavigation()
            }
        }
    }
}



@Composable
fun ProofAppNavigation() {
    val controller = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    NavHost(navController = controller,
        startDestination = Destinations.CAMERA){
        composable(Destinations.CAMERA) {
            CameraScreen(viewModel = cameraViewModel, onNavigateToPreview = {
                controller.navigate(Destinations.PREVIEW)
            })

        }

        composable(Destinations.PREVIEW) {
            MediaPreview(viewModel = cameraViewModel, onNavigateBack = {
                controller.popBackStack()
            })
        }

    }
}

object Destinations {
    const val CAMERA = "CAMERA"
    const val PREVIEW = "PREVIEW"
}
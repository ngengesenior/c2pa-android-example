package com.proofmode.c2pa.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.proofmode.c2pa.ui.theme.ProofmodeC2paTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProofmodeC2paTheme {
                ProofAppNavigation3()
            }
        }
    }
}



@Composable
fun ProofAppNavigation() {
    val controller = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    NavHost(navController = controller,
        startDestination = Destinations.Camera){
        composable<Destinations.Camera> {
            CameraScreen(viewModel = cameraViewModel, onNavigateToPreview = {
                controller.navigate(Destinations.Preview)
            }, onNavigateToSettings = {
                controller.navigate(Destinations.Settings)
            })

        }

        composable<Destinations.Preview> {
            MediaPreview(viewModel = cameraViewModel, onNavigateBack = {
                controller.popBackStack()
            })
        }
        composable<Destinations.Settings> {
            SettingsScreen(viewModel = settingsViewModel, onNavigateBack = {
                controller.popBackStack()
            })
        }

    }
}

@Composable
fun ProofAppNavigation3() {
    //val controller = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val backStack = remember { mutableStateListOf<AppDestination>(AppDestination.Camera) }

    NavDisplay(
        backStack = backStack,
        entryProvider = {key->
            when(key) {
                is AppDestination.Camera -> NavEntry(key) {
                    CameraScreen(viewModel = cameraViewModel, onNavigateToPreview = {
                        backStack.add(AppDestination.Preview)
                    }, onNavigateToSettings = {
                        backStack.add(AppDestination.Settings)
                    })
                }

                is AppDestination.Preview -> NavEntry(key) {
                    MediaPreview(viewModel = cameraViewModel, onNavigateBack = {
                        //backStack.removeLastOrNull()
                    })
                }
                is AppDestination.Settings -> NavEntry(key) {
                    SettingsScreen(viewModel = settingsViewModel, onNavigateBack = {
                        //backStack.removeLastOrNull()
                    })
                }
            }

        }
    )



    /*NavHost(navController = controller,
        startDestination = Destinations.Camera){
        composable<Destinations.Camera> {
            CameraScreen(viewModel = cameraViewModel, onNavigateToPreview = {
                controller.navigate(Destinations.Preview)
            }, onNavigateToSettings = {
                controller.navigate(Destinations.Settings)
            })

        }

        composable<Destinations.Preview> {
            MediaPreview(viewModel = cameraViewModel, onNavigateBack = {
                controller.popBackStack()
            })
        }
        composable<Destinations.Settings> {
            SettingsScreen(viewModel = settingsViewModel, onNavigateBack = {
                controller.popBackStack()
            })
        }

    }*/
}


object Destinations {
    @Serializable
    object Camera

    @Serializable
    object Preview

    @Serializable
    object Settings
}

@Serializable
sealed class AppDestination: NavKey {
    @Serializable
    object Camera: AppDestination(), NavKey
    @Serializable
    object Preview: AppDestination()
    @Serializable
    object Settings: AppDestination()

}



package com.amarchaud.paginationdemokmm

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amarchaud.ui.settings.AppSettingsAction
import com.amarchaud.ui.settings.AppSettingsViewModel
import com.amarchaud.ui.composables.LocalNavAnimatedVisibilityScope
import com.amarchaud.ui.composables.LocalSharedTransitionScope
import com.amarchaud.ui.screen.detail.UserDetailComposable
import com.amarchaud.ui.screen.mainList.MainListComposable
import com.amarchaud.ui.theme.PaginationDemoTheme
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

object MultiNav {
    @Serializable
    object Home

    @Serializable
    data class Detail(val id: Long)
}

private const val TRANSITION_MS = 350

@Composable
fun App() {
    // app-wide settings: the "Material You" option (Android 12+)
    val settingsViewModel = koinViewModel<AppSettingsViewModel>()
    val settings by settingsViewModel.state.collectAsState()

    PaginationDemoTheme(dynamicColor = settings.materialYou) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // wait for the stored option: the palette never flashes at startup
            if (settings.loaded) {
                AppNavigation(
                    materialYouEnabled = settings.materialYou,
                    onMaterialYouToggled = { enabled ->
                        settingsViewModel.onAction(AppSettingsAction.MaterialYouToggled(enabled))
                    },
                )
            }
        }
    }
}

@Composable
private fun AppNavigation(
    materialYouEnabled: Boolean,
    onMaterialYouToggled: (Boolean) -> Unit,
) {
    // the avatar morphs between the list and the detail screen
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = MultiNav.Home,
                enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                exitTransition = { fadeOut(tween(TRANSITION_MS)) },
                popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
                popExitTransition = { fadeOut(tween(TRANSITION_MS)) },
            ) {
                composable<MultiNav.Home> {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        MainListComposable(
                            materialYouEnabled = materialYouEnabled,
                            onMaterialYouToggled = onMaterialYouToggled,
                            onUserClick = { navController.navigate(MultiNav.Detail(it)) }
                        )
                    }
                }

                composable<MultiNav.Detail> {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        UserDetailComposable(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

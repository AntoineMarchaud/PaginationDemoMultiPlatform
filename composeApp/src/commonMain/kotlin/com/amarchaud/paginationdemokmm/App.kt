package com.amarchaud.paginationdemokmm

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amarchaud.ui.composables.LocalNavAnimatedVisibilityScope
import com.amarchaud.ui.composables.LocalSharedTransitionScope
import com.amarchaud.ui.screen.detail.UserDetailComposable
import com.amarchaud.ui.screen.mainList.MainListComposable
import com.amarchaud.ui.theme.PaginationDemoTheme
import kotlinx.serialization.Serializable

object MultiNav {
    @Serializable
    object Home

    @Serializable
    data class Detail(val id: Long)
}

private const val TRANSITION_MS = 350

@Composable
fun App() {
    PaginationDemoTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
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
    }
}

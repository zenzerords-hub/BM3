package com.example.buckmanager

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import android.app.Application
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.example.buckmanager.ui.screens.DashboardScreen
import com.example.buckmanager.viewmodel.MainViewModel

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DashboardCrashTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDashboardScreenRenders() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val repository = com.example.buckmanager.data.Repository(app)
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            DashboardScreen(viewModel, {}, {}, {}, {}, { _, _ -> }, { _ -> }, {}, {})
        }
    }
}

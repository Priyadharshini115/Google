package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SentimentViewModel
import com.example.ui.screens.AnalyzeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SamplesScreen
import com.example.ui.theme.SentilyticsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SentilyticsTheme {
                val viewModel: SentimentViewModel = viewModel()
                val activeTab by viewModel.activeTab.collectAsState()

                BackHandler(enabled = activeTab != 0) {
                    viewModel.setActiveTab(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "Sentilytics AI",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("main_bottom_nav"),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                selected = activeTab == 0,
                                onClick = { viewModel.setActiveTab(0) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 0) Icons.Filled.QuestionAnswer else Icons.Outlined.QuestionAnswer,
                                        contentDescription = "Review Answer"
                                    )
                                },
                                label = { Text("Answer", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("nav_tab_answer")
                            )

                            NavigationBarItem(
                                selected = activeTab == 1,
                                onClick = { viewModel.setActiveTab(1) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 1) Icons.Filled.Psychology else Icons.Outlined.Psychology,
                                        contentDescription = "Sentiment Lab"
                                    )
                                },
                                label = { Text("Lab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("nav_tab_lab")
                            )

                            NavigationBarItem(
                                selected = activeTab == 2,
                                onClick = { viewModel.setActiveTab(2) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 2) Icons.Filled.LibraryBooks else Icons.Outlined.LibraryBooks,
                                        contentDescription = "Sample Reviews"
                                    )
                                },
                                label = { Text("Samples", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("nav_tab_samples")
                            )

                            NavigationBarItem(
                                selected = activeTab == 3,
                                onClick = { viewModel.setActiveTab(3) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 3) Icons.Filled.History else Icons.Outlined.History,
                                        contentDescription = "History"
                                    )
                                },
                                label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("nav_tab_history")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (activeTab) {
                            0 -> HomeScreen(viewModel = viewModel)
                            1 -> AnalyzeScreen(viewModel = viewModel)
                            2 -> SamplesScreen(viewModel = viewModel)
                            3 -> HistoryScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

package com.example.ai.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppScaffold(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(vm: MainViewModel) {
    val uiState by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("詐欺ガード MVP") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CheckScreen(vm, uiState)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            HistoryScreen(uiState)
        }
    }
}

@Composable
fun CheckScreen(vm: MainViewModel, uiState: UiState) {
    var input by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("本文またはURLを入力") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                vm.analyzeAndSave(input, source = "Manual")
                input = ""
            },
            enabled = input.isNotBlank()
        ) {
            Text("解析する")
        }
        Spacer(Modifier.height(16.dp))

        uiState.lastResult?.let { result ->
            Text("最新の判定結果: スコア=${result.score} / ソース=${result.source}")
        }
    }
}

@Composable
fun HistoryScreen(uiState: UiState) {
    Text(
        text = "履歴",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn {
        items(uiState.history) { event ->
            Column(Modifier.padding(8.dp)) {
                Text("本文: ${event.text}")
                Text("スコア: ${event.score}")
                Text("ソース: ${event.source}")
                HorizontalDivider()
            }
        }
    }
}

package com.scamprevention.ai.ui.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scamprevention.ai.AppBuild   // ← ここを追加

class AdminModeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // リリースビルドでは表示しない
        if (!AppBuild.DEBUG) {
            finish()
            return
        }

        setContent {
            AdminModeScreen()
        }
    }
}

@Composable
fun AdminModeScreen() {
    var showAds by remember { mutableStateOf(true) }
    var showDonation by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("管理者モード", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("広告表示")
            Switch(
                checked = showAds,
                onCheckedChange = { showAds = it }
            )
        }

        if (showAds) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 8.dp),
            ) {
                Text("［広告エリア（ダミー）］")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("支援金表示")
            Switch(
                checked = showDonation,
                onCheckedChange = { showDonation = it }
            )
        }

        if (showDonation) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 8.dp),
            ) {
                Text("［支援金エリア（ダミー）］")
            }
        }
    }
}

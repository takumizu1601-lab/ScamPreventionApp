package com.scamprevention.ai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scamprevention.ai.ui.admin.AdminModeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("メイン画面", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            // 管理者モード画面を開く
                            startActivity(Intent(this@MainActivity, AdminModeActivity::class.java))
                        }) {
                            Text("管理者モード")
                        }
                    }
                }
            }
        }
    }
}

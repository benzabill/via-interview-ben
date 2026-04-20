package com.tseytlin.via.interview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tseytlin.via.interview.navigation.NavGraph
import com.tseytlin.via.interview.ui.theme.ViaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViaTheme {
                NavGraph()
            }
        }
    }
}

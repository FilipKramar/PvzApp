package com.example.pvzapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pvzapp.plant.PlantListActivity
import com.example.pvzapp.ui.theme.PvzAppTheme
import com.example.pvzapp.zombie.ZombieListActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import coil.compose.rememberImagePainter

class MainActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PvzAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityContent()
                }
            }
        }

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun MainActivityContent() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter("https://www.pngmart.com/files/22/Plants-Vs-Zombies-Logo-PNG-Photo.png"),
                contentDescription = "Plants vs. Zombies Logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val intent = Intent(context, PlantListActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5AF6AD),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
            ) {
                Text(text = "Go to Plant List", style = TextStyle(fontSize = 40.sp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, ZombieListActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF066666),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
            ) {
                Text(text = "Go to Zombie List", style = TextStyle(fontSize = 35.sp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivityContent() {
    PvzAppTheme {
        MainActivityContent()
    }
}

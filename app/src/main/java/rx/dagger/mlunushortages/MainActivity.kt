package rx.dagger.mlunushortages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import rx.dagger.mlunushortages.presentation.Screen
import rx.dagger.mlunushortages.presentation.theme.MlunuShortagesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MlunuShortagesTheme {
                Screen()
            }
        }
    }
}
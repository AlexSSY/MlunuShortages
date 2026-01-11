package rx.dagger.mlunushortages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import rx.dagger.mlunushortages.ui.theme.MlunuShortagesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ShortagesViewModel()
            val loading = viewModel.loadingStateFlowSafe.collectAsState()
            val shortages = viewModel.shortagesStateFlowSafe.collectAsState()
            val periodWithoutElectricity = viewModel.periodWithoutElectricityFlowSafe.collectAsState()

            MlunuShortagesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Row() {
                            Button(
                                onClick = { viewModel.update() }
                            ) {
                                Text("Update")
                            }
                        }
                        Row() {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(periodWithoutElectricity.value) {
                                    Text(it.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import rx.dagger.mlunushortages.domain.Shortages

@Composable
fun Screen() {
    val shortagesViewModel: ShortagesViewModel = koinViewModel()
    val shortages = shortagesViewModel.shortagesStateFlow.collectAsState()
    val timerState = shortagesViewModel.timerStateFlow.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GavWidget(
                modifier = Modifier
                    .fillMaxWidth(),
                isGav = shortages.value.isGav
            )
            TimerWidget(
                modifier = Modifier
                    .fillMaxWidth(),
                timerState = timerState.value
            )
        }
    }
}
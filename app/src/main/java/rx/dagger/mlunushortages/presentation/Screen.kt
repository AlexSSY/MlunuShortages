package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun Screen() {
    val shortagesViewModel: ShortagesViewModel = koinViewModel()
    val shortages = shortagesViewModel.shortagesStateFlow.collectAsState()
    val timerState = shortagesViewModel.timerStateFlow.collectAsState()
    val todayChartSectors = shortagesViewModel.todayChartSectors.collectAsState()
    val nowState = shortagesViewModel.nowFlow.collectAsState()
    val todayTotalShortages = shortagesViewModel.todayShortagesTotal.collectAsState()
    val tomorrowChartSectors = shortagesViewModel.tomorrowChartSectors.collectAsState()
    val tomorrowTotalShortages = shortagesViewModel.tomorrowShortagesTotal.collectAsState()
    val periodsWithElectricity = shortagesViewModel.periodsWithElectricity.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column() {
                    for (p in periodsWithElectricity.value) {
                        val from = p.start.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )

                        val to = p.end.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )

                        val duration = Duration.between(p.start, p.end).toMinutes() / 60F

                        Text(
                            text = "з $from до $to ($duration часа)"
                        )
                    }
                }
            }
            PowerOutageDonutChartWidget(
                todayDateTime = nowState.value,
                chartSectors = todayChartSectors.value,
                totalShortages = todayTotalShortages.value
            )
            tomorrowChartSectors.value?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "График на завтра:",
                    textAlign = TextAlign.Center
                )
                PowerOutageDonutChartWidget(
                    chartSectors = it,
                    totalShortages = tomorrowTotalShortages.value
                )
            }
        }
    }
}
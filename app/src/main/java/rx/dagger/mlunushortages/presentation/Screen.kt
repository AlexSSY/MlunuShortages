package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import rx.dagger.mlunushortages.domain.Shortages

@Composable
fun Screen() {
    val shortagesViewModel: ShortagesViewModel = koinViewModel()
    val shortages = shortagesViewModel.shortagesStateFlow.collectAsState()
    val timerState = shortagesViewModel.timerStateFlow.collectAsState()
    val todayChartSectors = shortagesViewModel.todayChartSectors.collectAsState()
    val tomorrowChartSectors = shortagesViewModel.tomorrowChartSectors.collectAsState()
    val nowState = shortagesViewModel.nowFlow.collectAsState()
    val todayTotalShortages = shortagesViewModel.todayShortagesTotal.collectAsState()
    val tomorrowTotalShortages = shortagesViewModel.tomorrowShortagesTotal.collectAsState()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentAlignment = Alignment.Center
            ) {
                PowerOutageDonutChart(
                    chartSectors = todayChartSectors.value,
                    textColor = Color.Black
                )
                TimeArrow(
                    modifier = Modifier.fillMaxSize(),
                    arrowColor = MaterialTheme.colorScheme.onBackground,
                    nowState.value
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        )
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("-${todayTotalShortages.value} +${24 - todayTotalShortages.value}")
                }
            }
            if (tomorrowChartSectors.value != null) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Tomorrow schedule:",
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PowerOutageDonutChart(
                        chartSectors = tomorrowChartSectors.value!!,
                        textColor = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-${tomorrowTotalShortages.value} +${24 - tomorrowTotalShortages.value}")
                    }
                }
            }
        }
    }
}
package rx.dagger.mlunushortages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import rx.dagger.mlunushortages.ui.theme.MlunuShortagesTheme
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ShortagesViewModel()
            val loading = viewModel.loadingStateFlowSafe.collectAsState()
            val shortages = viewModel.shortagesStateFlowSafe.collectAsState()
            val periodWithoutElectricity =
                viewModel.periodWithoutElectricityFlowSafe.collectAsState()
            val timerState = viewModel.timerStateFlowSafe.collectAsState()

            viewModel.update()

            MlunuShortagesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Row() {
                            TimerWidget(
                                modifier = Modifier.fillMaxWidth(),
                                timerState = timerState.value
                            )
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

@Composable
fun TimerWidget(modifier: Modifier, timerState: TimerState) {
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = Color(0xffdddddd),
                shape = shape
            )
            .background(
                color = Color.Transparent,
                shape = shape
            )
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row() {
                    Text(
                        text = "Свет должен быть",
                        fontSize = 12.sp
                    )
                }
                Row() {
                    Text("Отключение света через:")
                }
            }

            Column(
                modifier = Modifier.wrapContentWidth()
            ) {
                var now by remember { mutableStateOf(LocalDateTime.now()) }

                LaunchedEffect(timerState.timeRemaining) {
                    while (timerState.timeRemaining != null) {
                        now = LocalDateTime.now()
                        delay(1000L)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (timerState.timeRemaining != null) {
                        val distance = Duration.between(now, timerState.timeRemaining)

                        val totalSeconds = max(0, distance.seconds)

                        val hours = totalSeconds / 3600
                        val minutes = (totalSeconds % 3600) / 60
                        val seconds = totalSeconds % 60

                        NumberBox(value = hours, label = "ЧАС")
                        NumberBox(value = minutes, label = "МИН")
                        NumberBox(value = seconds, label = "СЕК")
                    } else {
                        Text("Нет данных")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberBox(value: Long, label: String) {
    Column(
        modifier = Modifier
            .background(
                color = Color(0xfff0ebe2),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .size(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = value.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 8.sp,
                lineHeight = 6.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
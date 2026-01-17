package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max

@Composable
fun TimerWidget(modifier: Modifier, timerState: TimerState) {
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = shape
            )
            .background(
                color = Color.Transparent,
                shape = shape
            )
            .padding(
                start = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
                end = 8.dp
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isElectricity = timerState.isElectricityAvailable

                Row() {
                    Text(
                        text = if (isElectricity) "Свет должен быть" else "Света не должно быть",
                        fontSize = 12.sp,
                        lineHeight = 1.sp,
                        fontWeight = FontWeight.Bold  ,
                        color = if (isElectricity) Color.Green else Color.Red
                    )
                }
                Row() {
                    Text(
                        text = if (isElectricity) "Выключат через:" else "Включат через:",
                        lineHeight = 1.sp
                    )
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
                        Text("No Data")
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
                color = MaterialTheme.colorScheme.surfaceContainer,
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

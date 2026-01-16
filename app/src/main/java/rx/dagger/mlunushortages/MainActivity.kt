package rx.dagger.mlunushortages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import rx.dagger.mlunushortages.presentation.TimerState
import rx.dagger.mlunushortages.ui.theme.MlunuShortagesTheme
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shortagesViewModel: ShortagesViewModel = koinViewModel()
            val periodWithoutElectricity =
                shortagesViewModel.periodsActualFlow.collectAsState(emptyList())
            val todayPeriods = shortagesViewModel.todayPeriods.collectAsState()
            val timerState = shortagesViewModel.timerStateFlowSafe.collectAsState()
            val nowState = shortagesViewModel.nowFlow.collectAsState(LocalDateTime.now())
            val todayTotalShortages = shortagesViewModel.todayShortagesTotal.collectAsState()
            val isGav = shortagesViewModel.isGav.collectAsState(false)

            MlunuShortagesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Row() {
                            GavWidget(
                                modifier = Modifier.fillMaxWidth(),
                                isGav = isGav.value
                            )
                        }
                        Row() {
                            TimerWidget(
                                modifier = Modifier.fillMaxWidth(),
                                timerState = timerState.value
                            )
                        }
//                        Row() {
//                            LazyColumn(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                            ) {
//                                items(periodWithoutElectricity.value) {
//                                    Text(it.toString())
//                                }
//                            }
//                        }
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(220.dp),
//                            horizontalArrangement = Arrangement.Center,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Canvas(
//                                modifier = Modifier.size(200.dp)
//                            ) {
//                                drawCircle(
//                                    color = Color.Red,
//                                    radius = size.minDimension / 2
//                                )
//                            }
//                        }
//                        Row() {
//                            PowerOutagePieChart(
//                                periodWithoutElectricity.value
//                            )
//                        }
                        Row() {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PowerOutageDonutChart(
                                    periods = todayPeriods.value,
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
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    shortagesViewModel.update()
                                }
                            ) {
                                Text(
                                    text = "updateAndNotify"
                                )
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
                color = MaterialTheme.colorScheme.surfaceContainer,
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isElectricity = timerState.isElectricityAvailable

                Row() {
                    Text(
                        text = if (isElectricity) "Свет должен быть" else "Света не должно быть",
                        fontSize = 12.sp,
                        lineHeight = 1.sp
                    )
                }
                Row() {
                    Text(
                        text = if (isElectricity) "Отключат через:" else "Включат через:",
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

@Composable
fun PowerOutagePieChart(
    periods: List<PeriodWithoutElectricity>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(200.dp)
    ) {
        val totalMinutesInDay = 24 * 60

        periods.forEach { period ->
            val startMinutes = period.from.toMinutesOfDay()
            val endMinutes = period.to.toMinutesOfDay()

            val durationMinutes =
                if (endMinutes >= startMinutes) {
                    endMinutes - startMinutes
                } else {
                    // период через полночь
                    (totalMinutesInDay - startMinutes) + endMinutes
                }

            val startAngle = (startMinutes / totalMinutesInDay.toFloat()) * 360f - 90f
            val sweepAngle = (durationMinutes / totalMinutesInDay.toFloat()) * 360f

            drawArc(
                color = Color.Red,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
        }
    }
}

private const val HOURS_IN_DAY = 24
private const val DEGREES_PER_HOUR = 360f / HOURS_IN_DAY

fun hourToAngle(hour: Int): Float =
    hour * DEGREES_PER_HOUR - 90f

@Composable
fun PowerOutageDonutChart(
    periods: List<PeriodWithoutElectricity>,
    modifier: Modifier = Modifier,
    textColor: Color
) {
    Canvas(
        modifier = modifier
            .size(320.dp)
    ) {
        val center = center
        val radius = size.minDimension / 2

        val outerStroke = 72f//36f
        val innerStroke = 72f//36f

        // ─────────────────────────────
        // 1️⃣ ВНЕШНИЙ КРУГ (часы)
        // ─────────────────────────────
        repeat(24) { hour ->
            drawArc(
                color = Color(0xFFAEEED0),
                startAngle = hourToAngle(hour),
                sweepAngle = DEGREES_PER_HOUR,
                useCenter = false,
                style = Stroke(
                    width = outerStroke,
                    cap = StrokeCap.Butt
                ),
                size = Size(
                    (radius * 2) - outerStroke,
                    (radius * 2) - outerStroke
                ),
                topLeft = Offset(
                    outerStroke / 2,
                    outerStroke / 2
                )
            )
        }

        // ─────────────────────────────
        // 2️⃣ КРАСНЫЕ ПЕРИОДЫ
        // ─────────────────────────────
        periods.forEach { period ->
            val startHour = period.from.hour + period.from.minute / 60f
            val endHour = period.to.hour + period.to.minute / 60f

            val sweepHours =
                if (endHour >= startHour) {
                    endHour - startHour
                } else {
                    (24 - startHour) + endHour
                }

            drawArc(
                color = Color(0xFFD16A6A),
                startAngle = startHour * DEGREES_PER_HOUR - 90f,
                sweepAngle = sweepHours * DEGREES_PER_HOUR,
                useCenter = false,
                style = Stroke(
                    width = innerStroke,
                    cap = StrokeCap.Butt
                ),
                size = Size(
                    (radius * 2) - outerStroke,
                    (radius * 2) - outerStroke
                ),
                topLeft = Offset(
                    outerStroke / 2,
                    outerStroke / 2
                )
            )
        }

        // ─────────────────────────────
        // 3️⃣ ЧАСЫ ПО ОКРУЖНОСТИ
        // ─────────────────────────────
        repeat(24) { hour ->
            val angleRad = Math.toRadians(hourToAngle(hour).toDouble())

            val textRadius = radius - outerStroke / 2
            val x = center.x + cos(angleRad).toFloat() * textRadius
            val y = center.y + sin(angleRad).toFloat() * textRadius

            drawContext.canvas.nativeCanvas.drawText(
                hour.toString(),
                x,
                y + 12f,
                android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 32f
                    color = textColor.toArgb()
                    isFakeBoldText = true
                }
            )
        }
    }
}

fun timeToAngle(now: LocalDateTime): Float {
    val minutes = now.hour * 60 + now.minute + now.second / 60f
    return (minutes / (24f * 60f)) * 360f - 90f
}

@Composable
fun TimeArrow(
    modifier: Modifier = Modifier,
    arrowColor: Color = Color.Black,
    now: LocalDateTime
) {
    Canvas(modifier = modifier) {
        val center = size.center
        val radius = size.minDimension / 2 * 0.9f

        val angleRad = Math.toRadians(timeToAngle(now).toDouble())

        val endX = center.x + cos(angleRad).toFloat() * radius
        val endY = center.y + sin(angleRad).toFloat() * radius

        // основная линия
        drawLine(
            color = arrowColor,
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // кружок в центре
        drawCircle(
            color = arrowColor,
            radius = 6.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun GavWidget(
    modifier: Modifier = Modifier,
    isGav: Boolean
) {
    if (isGav) {
        val shape = RoundedCornerShape(8.dp)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(
                    color= MaterialTheme.colorScheme.surfaceContainer,
                    width = 1.dp,
                    shape = shape
                )
                .background(
                    color = Color(0xffffc9c9),
                    shape = shape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Дейсивует граффик аварийных отключений (ГАВ)",
                fontSize = 14.sp
            )
        }
    }
}
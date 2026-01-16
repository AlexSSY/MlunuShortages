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
import rx.dagger.mlunushortages.presentation.PeriodWithoutElectricity
import rx.dagger.mlunushortages.presentation.Screen
import rx.dagger.mlunushortages.presentation.ShortagesViewModel
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
//            val periodWithoutElectricity =
//                shortagesViewModel.periodsActualFlow.collectAsState(emptyList())
//            val todayPeriods = shortagesViewModel.todayPeriods.collectAsState()
//            val timerState = shortagesViewModel.timerStateFlowSafe.collectAsState()
//            val nowState = shortagesViewModel.nowFlow.collectAsState(LocalDateTime.now())
//            val todayTotalShortages = shortagesViewModel.todayShortagesTotal.collectAsState()
//            val isGav = shortagesViewModel.isGav.collectAsState(false)

            MlunuShortagesTheme {
                Screen()
                /*
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
                }*/
            }
        }
    }
}
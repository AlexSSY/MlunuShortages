package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PowerOutageDonutChartWidget(
    todayDateTime: LocalDateTime? = null,
    todayChartSectors: List<ChartSector>,
    todayTotalShortages: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
        contentAlignment = Alignment.Center
    ) {
        DonutChart(
            chartSectors = todayChartSectors,
            textColor = Color.White
        )
        todayDateTime?.let {
            TimeArrow(
                modifier = Modifier.fillMaxSize(),
                arrowColor = Color(0xFFFFE66B),
                it
            )
        }
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = Color(0xFFFFE66B),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "-${todayTotalShortages}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+${24 - todayTotalShortages}",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private const val HOURS_IN_DAY = 24
private const val DEGREES_PER_HOUR = 360f / HOURS_IN_DAY

fun hourToAngle(hour: Int): Float =
    hour * DEGREES_PER_HOUR - 90f

@Composable
fun DonutChart(
    chartSectors: List<ChartSector>,
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
                color = Color(0xFF008A54),
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
        chartSectors.forEach { sector ->
//            val startHour = period.from.hour + period.from.minute / 60f
//            val endHour = period.to.hour + period.to.minute / 60f
            val startHour = sector.startHour / 60F
            val endHour = sector.endHour / 60F

            val sweepHours =
                if (endHour >= startHour) {
                    endHour - startHour
                } else {
                    (24 - startHour) + endHour
                }

            drawArc(
                color = Color(0xFF814141),
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

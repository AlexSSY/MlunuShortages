package rx.dagger.mlunushortages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                    color= Color(0xFF913434),
                    width = 1.dp,
                    shape = shape
                )
                .background(
                    color = Color(0xFF913434),
                    shape = shape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Аварийные отключения",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
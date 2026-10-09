package com.example.emgcompanion.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.emgcompanion.domain.MeasurementSample
import com.example.emgcompanion.domain.MeasurementSource
import kotlin.math.abs

@Composable
fun EmgScreen(viewModel: EmgViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "FIELD LAB  /  EMG",
                color = colors.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
            )
            Text(
                text = "Muskelaktivität",
                color = colors.onBackground,
                fontSize = 29.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Signalübersicht in Echtzeit",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilterChip(
                selected = state.source == MeasurementSource.DEMO,
                onClick = { viewModel.selectSource(MeasurementSource.DEMO) },
                label = { Text("Demo") },
                enabled = !state.isActive(),
            )
            FilterChip(
                selected = state.source == MeasurementSource.DEVICE,
                onClick = { viewModel.selectSource(MeasurementSource.DEVICE) },
                label = { Text("BLE-Gerät") },
                enabled = !state.isActive(),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "AKTUELLER MESSWERT",
                        color = colors.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                    )
                    StatusIndicator(state.status)
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = state.latestSample?.microvolts?.toInt()?.toString() ?: "–––",
                        color = colors.onSurface,
                        fontSize = 54.sp,
                        lineHeight = 58.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "µV",
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = colors.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = statusLabel(state.status),
                    color = colors.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Signalverlauf",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Amplitude · µV",
                            color = colors.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                    Text(
                        text = if (state.source == MeasurementSource.DEMO) "SYNTHETISCH" else "GERÄT",
                        color = colors.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(174.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Waveform(
                        samples = state.samples,
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (state.samples.isEmpty()) {
                        Surface(
                            color = colors.surface,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = "Starte eine Messung, um den Verlauf anzuzeigen.",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                color = colors.onSurfaceVariant,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }

        state.errorMessage?.let { errorMessage ->
            Surface(
                color = colors.error.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(14.dp),
                    color = colors.error,
                    fontSize = 13.sp,
                )
            }
        }

        Button(
            onClick = viewModel::start,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = !state.isActive(),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
            ),
        ) {
            Text(
                text = if (state.source == MeasurementSource.DEMO) "Simulation starten" else "Gerät verbinden",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }
        OutlinedButton(
            onClick = viewModel::stop,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.isActive(),
            shape = RoundedCornerShape(17.dp),
        ) {
            Text("Messung stoppen", fontWeight = FontWeight.SemiBold)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(colors.secondary),
            )
            Text(
                text = "Demo-Werte sind synthetisch und nicht für medizinische Entscheidungen bestimmt. Die BLE-Gerätespezifikation ist noch offen.",
                color = colors.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun StatusIndicator(status: SessionStatus) {
    val colors = MaterialTheme.colorScheme
    val indicatorColor = when (status) {
        SessionStatus.IDLE -> colors.onSurfaceVariant
        SessionStatus.CONNECTING -> colors.secondary
        SessionStatus.STREAMING -> colors.primary
        SessionStatus.FAILED -> colors.error
    }
    Surface(
        color = indicatorColor.copy(alpha = 0.12f),
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(indicatorColor),
            )
            Text(
                text = statusLabel(status).uppercase(),
                color = indicatorColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
            )
        }
    }
}

private fun statusLabel(status: SessionStatus): String = when (status) {
    SessionStatus.IDLE -> "Bereit"
    SessionStatus.CONNECTING -> "Verbindungsaufbau"
    SessionStatus.STREAMING -> "Messung aktiv"
    SessionStatus.FAILED -> "Verbindung fehlgeschlagen"
}

@Composable
private fun Waveform(
    samples: List<MeasurementSample>,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Kurvenverlauf mit ${samples.size} Messpunkten"
        },
    ) {
        val inset = 8.dp.toPx()
        val centerY = size.height / 2f
        val gridColor = colors.onSurfaceVariant.copy(alpha = 0.15f)
        for (line in 0..4) {
            val y = size.height * line / 4f
            drawLine(
                color = gridColor,
                start = Offset(inset, y),
                end = Offset(size.width - inset, y),
                strokeWidth = 1.dp.toPx(),
            )
        }
        drawLine(
            color = colors.onSurfaceVariant.copy(alpha = 0.2f),
            start = Offset(inset, centerY),
            end = Offset(size.width - inset, centerY),
            strokeWidth = 1.dp.toPx(),
        )
        if (samples.isEmpty()) return@Canvas

        val amplitude = samples.maxOf { abs(it.microvolts) }.coerceAtLeast(500.0)
        val drawableHeight = centerY - 12.dp.toPx()
        val path = Path()
        samples.forEachIndexed { index, sample ->
            val progress = if (samples.size == 1) 1f else index.toFloat() / (samples.lastIndex)
            val x = inset + progress * (size.width - inset * 2f)
            val y = centerY - (sample.microvolts / amplitude).toFloat() * drawableHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = colors.primary,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

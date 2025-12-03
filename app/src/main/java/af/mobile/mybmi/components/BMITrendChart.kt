package af.mobile.mybmi.components

import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.theme.BrandPrimary
import af.mobile.mybmi.theme.BrandSecondary
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BMITrendChart(
    history: List<BMICheckSummary>
) {
    // Ambil 7 data terakhir untuk ditampilkan di grafik
    val dataPoints = remember(history) {
        history.sortedBy { it.timestamp }.takeLast(7)
    }

    if (dataPoints.size < 2) return // Butuh minimal 2 titik untuk garis

    // Animasi Progress (0f -> 1f)
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500) // Durasi animasi 1.5 detik
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Tren BMI Anda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Cari min/max BMI untuk skala grafik
                    val maxBMI = dataPoints.maxOf { it.bmi }.toFloat() + 1f
                    val minBMI = (dataPoints.minOf { it.bmi }.toFloat() - 1f).coerceAtLeast(0f)
                    val bmiRange = maxBMI - minBMI

                    val points = mutableListOf<Offset>()
                    val xStep = width / (dataPoints.size - 1)

                    // Hitung koordinat titik
                    dataPoints.forEachIndexed { index, summary ->
                        val x = index * xStep
                        // Normalisasi Y (BMI tinggi ada di atas, jadi height - value)
                        val normalizedY = (summary.bmi.toFloat() - minBMI) / bmiRange
                        val y = height - (normalizedY * height)
                        points.add(Offset(x, y))
                    }

                    // --- GAMBAR GARIS ---
                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, points.first().y)
                            // Buat garis kurva halus (Bezier)
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                // Control point untuk kurva
                                val cx1 = (p1.x + p2.x) / 2
                                val cy1 = p1.y
                                val cx2 = (p1.x + p2.x) / 2
                                val cy2 = p2.y
                                cubicTo(cx1, cy1, cx2, cy2, p2.x, p2.y)
                            }
                        }
                    }

                    // PathMeasure untuk animasi "menggambar garis"
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(path, false)

                    val animatedPath = Path()
                    // Potong path sesuai progress animasi
                    pathMeasure.getSegment(0f, pathMeasure.length * animationProgress.value, animatedPath, true)

                    // Draw Garis
                    drawPath(
                        path = animatedPath,
                        color = BrandPrimary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // --- GAMBAR AREA GRADIEN DI BAWAH GARIS ---
                    if (animationProgress.value > 0.1f) {
                        val fillPath = Path().apply {
                            addPath(animatedPath)
                            // Tutup path ke bawah agar bisa diisi warna
                            if (points.isNotEmpty()) {
                                // Titik terakhir yang sudah digambar
                                val currentEndPos = pathMeasure.getPosition(pathMeasure.length * animationProgress.value)
                                lineTo(currentEndPos.x, height)
                                lineTo(0f, height)
                                close()
                            }
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(BrandPrimary.copy(alpha = 0.3f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )
                    }

                    // --- GAMBAR TITIK DATA (DOTS) ---
                    // Muncul satu per satu sesuai progress X
                    points.forEachIndexed { index, offset ->
                        val progressThreshold = index.toFloat() / (points.size - 1)
                        if (animationProgress.value >= progressThreshold) {
                            drawCircle(
                                color = BrandSecondary,
                                radius = 4.dp.toPx(),
                                center = offset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = offset
                            )
                        }
                    }
                }
            }
        }
    }
}
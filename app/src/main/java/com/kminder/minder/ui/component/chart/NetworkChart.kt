package com.kminder.minder.ui.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionType
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.util.EmotionUiUtil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Refined Word Cloud Chart
 *
 * Requirements:
 * 1. All text color: Black.
 * 2. Algorithm: Greedy Placement.
 * 3. Rotation: 0, 90, -90 degrees allowed.
 * 4. Hierarchy (Size):
 *    1) Final Emotion
 *    2) Primary / Secondary Emotions
 *    3) Other Emotion Types (found in keywords)
 *    4) Keywords
 */

private data class WordItem(
    val text: String,
    val fontSizeSp: Float,
    val fontWeight: FontWeight,
    val type: ItemType,
    val rotation: Float = 0f // 0f, 90f, -90f
)

private enum class ItemType {
    FINAL,      // Level 1
    MAIN,       // Level 2 (Primary/Secondary)
    OTHER,      // Level 3 (Other Emotions)
    KEYWORD     // Level 4
}

private data class PlacedItem(
    val item: WordItem,
    val position: Offset, // Center position
    val layout: TextLayoutResult,
    val bounds: Rect
)

@Composable
fun NetworkChart(
    result: EmotionResult,
    modifier: Modifier = Modifier,
    onNodeClick: (EmotionResult) -> Unit = {}
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

//    val result = remember(analysis) {
//        PlutchikEmotionCalculator.analyzeDominantEmotionCombination(analysis)
//    }

    val analysis = result.source

    // 1. Prepare Data Items with 4-Level Hierarchy
    val items = remember(analysis, result) {
        val list = mutableListOf<WordItem>()
        val random = Random(System.currentTimeMillis())

        // Helper for random rotation
        fun getRandomRotation(): Float {
            return when (random.nextInt(3)) {
                0 -> 0f
                1 -> 90f
                else -> -90f
            }
        }

        // Level 1: Final Emotion
        list.add(
            WordItem(
                text = EmotionUiUtil.getLabel(result, stringProvider),
                fontSizeSp = 40f,
                fontWeight = FontWeight.Black,
                type = ItemType.FINAL,
                rotation = 0f // Center is usually horizontal for readability, or random? User said "text isn't necessarily normal direction", but Center is best horizontal. Let's keep Center horizontal.
            )
        )

        // Level 2: Primary / Secondary Emotions
        val primary = result.primaryEmotion
        val secondary = result.secondaryEmotion
        val mainEmotions = listOfNotNull(primary, secondary)

        mainEmotions.forEach { emotion ->
            list.add(
                WordItem(
                    text = stringProvider.getEmotionName(emotion),
                    fontSizeSp = 28f,
                    fontWeight = FontWeight.ExtraBold,
                    type = ItemType.MAIN,
                    rotation = getRandomRotation()
                )
            )
        }

        // Level 3: Other Emotion Types (found in keywords but not main)
        val keywordsByEmotion = analysis.keywords.groupBy { it.emotion }
        val otherEmotionTypes = keywordsByEmotion.keys.filter { it !in mainEmotions }

        otherEmotionTypes.forEach { emotion ->
            list.add(
                WordItem(
                    text = stringProvider.getEmotionName(emotion),
                    fontSizeSp = 22f,
                    fontWeight = FontWeight.Bold,
                    type = ItemType.OTHER,
                    rotation = getRandomRotation()
                )
            )
        }

        // Level 4: Keywords
        // Flatten keywords, ideally distinct?
        val allKeywords = analysis.keywords.sortedByDescending { it.score } // Higher score first
        allKeywords.forEach { keyword ->
            // Size variation within keywords based on score
            val size = 10f + (keyword.score * 8f) // 14 ~ 18sp
            list.add(
                WordItem(
                    text = keyword.word,
                    fontSizeSp = size,
                    fontWeight = FontWeight.Medium,
                    type = ItemType.KEYWORD,
                    rotation = getRandomRotation()
                )
            )
        }

        list
    }

    BoxWithConstraints(modifier = modifier) {
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        val centerX = width / 2
        val centerY = height / 2

        // 2. Calculate Layout
        val placedItems = remember(items, width, height) {
            if (width <= 0 || height <= 0) return@remember emptyList<PlacedItem>()

            val placed = mutableListOf<PlacedItem>()
            val occupiedRects = mutableListOf<Rect>()
            
            // Spiral Parameters
            val angleStep = 0.1f
            val radiusStep = 1f

            items.forEachIndexed { index, item ->
                // Measure text
                val textStyle = TextStyle(
                    fontSize = item.fontSizeSp.sp,
                    fontWeight = item.fontWeight,
                    color = Color.Black // Requirement 1: All Black
                )
                val textLayout = textMeasurer.measure(
                    text = item.text,
                    style = textStyle
                )
                
                // Effective Dimensions considering rotation
                val w = textLayout.size.width.toFloat()
                val h = textLayout.size.height.toFloat()
                
                // If rotated 90 or -90, width and height swap for the bounding box
                val isRotated = item.rotation != 0f
                val boxW = if (isRotated) h else w
                val boxH = if (isRotated) w else h
                
                val padding = 2f

                if (index == 0) {
                    // Center Placement
                    val pos = Offset(centerX, centerY)
                    val rect = Rect(
                        centerX - boxW / 2 - padding,
                        centerY - boxH / 2 - padding,
                        centerX + boxW / 2 + padding,
                        centerY + boxH / 2 + padding
                    )
                    placed.add(PlacedItem(item, pos, textLayout, rect))
                    occupiedRects.add(rect)
                } else {
                    // Greedy Spiral
                    var currentAngle = 0.0
                    var currentRadius = 0.0
                    var found = false
                    
                    var safetyCounter = 0
                    val maxIterations = 3000

                    while (!found && safetyCounter < maxIterations) {
                        safetyCounter++
                        
                        // Spiral Position
                        val x = centerX + (currentRadius * cos(currentAngle)).toFloat()
                        val y = centerY + (currentRadius * sin(currentAngle)).toFloat()
                        
                        // Proposed Bound
                        val proposedRect = Rect(
                            x - boxW / 2 - padding,
                            y - boxH / 2 - padding,
                            x + boxW / 2 + padding,
                            y + boxH / 2 + padding
                        )

                        // Collision
                        var collision = false
                        for (existing in occupiedRects) {
                            if (existing.overlaps(proposedRect)) {
                                collision = true
                                break
                            }
                        }

                        if (!collision) {
                            placed.add(PlacedItem(item, Offset(x, y), textLayout, proposedRect))
                            occupiedRects.add(proposedRect)
                            found = true
                        } else {
                            currentAngle += angleStep
                            currentRadius = radiusStep * currentAngle
                        }
                    }
                }
            }
            placed
        }

        // 3. Calculate Scale to Fit
        // Since the spiral might grow beyond the view bounds (e.g., circular spiral in wide view),
        // we calculate the total bounds of placed items and scale down if necessary.
        val layoutScale = remember(placedItems, width, height) {
            if (placedItems.isEmpty()) return@remember 1f

            var minX = Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var maxX = Float.MIN_VALUE
            var maxY = Float.MIN_VALUE

            placedItems.forEach { item ->
                minX = minOf(minX, item.bounds.left)
                minY = minOf(minY, item.bounds.top)
                maxX = maxOf(maxX, item.bounds.right)
                maxY = maxOf(maxY, item.bounds.bottom)
            }

            val contentWidth = maxX - minX
            val contentHeight = maxY - minY
            
            // Add some padding (e.g., 10%)
            val targetScaleX = if (contentWidth > 0) (width * 0.9f) / contentWidth else 1f
            val targetScaleY = if (contentHeight > 0) (height * 0.9f) / contentHeight else 1f
            
            // Use the smaller scale to fit both dimensions, capped at 1f (don't scale up)
            val minScale = if (targetScaleX < targetScaleY) targetScaleX else targetScaleY
            if (minScale < 1f) minScale else 1f
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(placedItems) {
                    detectTapGestures { tapOffset ->
                        // Reverse scale to find hit
                        // hitPoint = (tapPoint - center) / layoutScale + center
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val adjustedPoint = (tapOffset - center) / layoutScale + center
                        
                        val centerItem = placedItems.firstOrNull { it.item.type == ItemType.FINAL }
                        if (centerItem != null && centerItem.bounds.contains(adjustedPoint)) {
                            onNodeClick(result)
                        }
                    }
                }
        ) {
            // Apply calculating scaling
            // Pivot is center by default, which matches our spiral center
            scale(scale = layoutScale) {
                placeWordCloudItems(placedItems)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.placeWordCloudItems(
    items: List<PlacedItem>
) {
    items.forEach { placed ->
        val rotation = placed.item.rotation
        
        // Rotate around the center position of the item
        rotate(degrees = rotation, pivot = placed.position) {
            // Draw text centered at position
            // Since we engaged rotation, we draw as if unrotated at the pivot
            val x = placed.position.x - placed.layout.size.width / 2
            val y = placed.position.y - placed.layout.size.height / 2
            
            drawText(
                textLayoutResult = placed.layout,
                topLeft = Offset(x, y)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFe5e4c8)
@Composable
fun PreviewWordCloudChart() {
    val mockEntry = MockData.mockJournalEntries[24] 
    mockEntry.emotionResult?.let {
        NetworkChart(result = it, modifier = Modifier.fillMaxSize())
    }
}
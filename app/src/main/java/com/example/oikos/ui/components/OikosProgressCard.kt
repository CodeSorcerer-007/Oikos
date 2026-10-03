package com.example.oikos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.theme.OikosError
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import com.example.oikos.theme.OikosWarmHoney
import com.example.oikos.theme.OikosWarmHoneyContainer
import com.example.oikos.util.CurrencyFormatter

@Composable
fun OikosProgressCard(
    items: List<GroceryItem>,
    budget: Double?,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val totalCount = items.size
    val completedCount = items.count { it.isCompleted }
    val remainingCount = totalCount - completedCount
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "progress"
    )

    val estimatedTotal = items.sumOf { (it.displayPrice ?: 0.0) * it.quantity }
    val isOverBudget = budget != null && estimatedTotal > budget

    val progressColor by animateColorAsState(
        targetValue = if (progress >= 1f && totalCount > 0) OikosForestGreen else if (isOverBudget) OikosWarmHoney else OikosForestGreen,
        label = "progress_color"
    )

    // Contextual insight
    val pendingByCategory = items.filter { !it.isCompleted }.groupBy { it.category }
    val topPendingCategory = pendingByCategory.maxByOrNull { it.value.size }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = OikosSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Count & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (totalCount == 0) "No items yet" 
                               else if (remainingCount == 0) "All $totalCount items checked! 🎉" 
                               else "$completedCount of $totalCount completed",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OikosTextPrimary
                        )
                    )
                    if (topPendingCategory != null && remainingCount > 0) {
                        Text(
                            text = "${topPendingCategory.value.size} items left in ${topPendingCategory.key.displayName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OikosTextSecondary
                            )
                        )
                    }
                }

                // Progress Percentage Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (progress >= 1f && totalCount > 0) OikosForestGreenContainer else OikosSurfaceBorder.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (progress >= 1f && totalCount > 0) OikosForestGreen else OikosTextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = OikosSurfaceBorder.copy(alpha = 0.5f)
            )

            // Price / Budget Section
            if (estimatedTotal > 0.0 || budget != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estimated Total
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Est. Total: ",
                            style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                        )
                        Text(
                            text = CurrencyFormatter.format(estimatedTotal, currencySymbol),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OikosTextPrimary
                            )
                        )
                    }

                    // Budget Tracker
                    if (budget != null) {
                        val remaining = budget - estimatedTotal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (remaining >= 0) "Budget: " else "Over by: ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isOverBudget) OikosError else OikosTextSecondary
                                )
                            )
                            Text(
                                text = if (remaining >= 0) 
                                    CurrencyFormatter.format(budget, currencySymbol) + " (${CurrencyFormatter.format(remaining, currencySymbol)} left)"
                                else 
                                    CurrencyFormatter.format(-remaining, currencySymbol),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isOverBudget) OikosError else OikosForestGreen
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

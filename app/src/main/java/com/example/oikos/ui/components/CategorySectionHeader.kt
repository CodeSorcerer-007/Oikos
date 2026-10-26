package com.example.oikos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary

@Composable
fun CategorySectionHeader(
    category: GroceryCategory,
    itemCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        label = "collapse_arrow"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(category.getAdaptiveTagBgColor())
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = category.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = category.getAdaptiveTagTextColor(),
                    letterSpacing = 0.8.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Item Count
        Text(
            text = "$itemCount item${if (itemCount > 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = OikosTextTertiary,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Toggle Expand Arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = OikosTextTertiary,
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation)
        )
    }
}

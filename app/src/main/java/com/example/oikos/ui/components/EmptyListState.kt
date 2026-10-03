package com.example.oikos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmptyListState(
    onAddSuggestedItem: (GroceryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickStarters = listOf(
        Triple("Farm Fresh Milk", 2.0 to "L", GroceryCategory.DAIRY),
        Triple("Brown Eggs", 12.0 to "pcs", GroceryCategory.DAIRY),
        Triple("Sourdough Bread", 1.0 to "loaf", GroceryCategory.BAKERY),
        Triple("Bananas", 1.0 to "bunch", GroceryCategory.PRODUCE),
        Triple("Vine Tomatoes", 1.0 to "kg", GroceryCategory.PRODUCE),
        Triple("Arabica Coffee", 1.0 to "pack", GroceryCategory.BEVERAGES),
        Triple("Extra Virgin Olive Oil", 1.0 to "L", GroceryCategory.PANTRY)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(OikosForestGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingBag,
                contentDescription = null,
                tint = OikosForestGreen,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Your list is fresh & ready",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OikosTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Type in the box above to add anything with natural language, or tap common pantry essentials below:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = OikosTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Suggested Essential Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickStarters.forEach { (name, qtyUnit, category) ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onAddSuggestedItem(
                                GroceryItem(
                                    name = name,
                                    quantity = qtyUnit.first,
                                    unit = qtyUnit.second,
                                    category = category
                                )
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = OikosSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
                ) {
                    Text(
                        text = "+ $name",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = OikosTextPrimary
                        )
                    )
                }
            }
        }
    }
}

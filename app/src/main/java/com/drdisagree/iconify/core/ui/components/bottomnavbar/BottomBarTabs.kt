package com.drdisagree.iconify.core.ui.components.bottomnavbar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.iconify.app.navigation.NavRoutes
import com.drdisagree.iconify.core.ui.components.others.withHaptic
import kotlin.math.roundToInt

@Composable
fun BottomBarTabs(
    tabs: List<NavRoutes.BottomBarTab>,
    selectedTab: Int,
    isTabEnabled: (NavRoutes.BottomBarTab) -> Boolean,
    onTabSelected: (NavRoutes.BottomBarTab) -> Unit,
    maxScreenFraction: Float = 0.7f
) {
    val containerSize = LocalWindowInfo.current.containerSize
    val screenWidth = containerSize.width
    val maxTotalWidth = screenWidth * maxScreenFraction

    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        LocalContentColor provides MaterialTheme.colorScheme.onSurface
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
            SubcomposeLayout { constraints ->
                val tabPlaceables = tabs.map { tab ->
                    val measurables = subcompose(tab) {
                        TabContent(
                            tab = tab,
                            selected = selectedTab == tabs.indexOf(tab),
                            enabled = isTabEnabled(tab),
                            onTabSelected = onTabSelected
                        )
                    }
                    measurables.first().measure(constraints)
                }

                val naturalMaxWidth = tabPlaceables.maxOf { it.width }

                val maxTotalWidthPx = maxTotalWidth.roundToInt()
                val tabCount = tabs.size
                val finalTabWidth =
                    (naturalMaxWidth * tabCount).coerceAtMost(maxTotalWidthPx) / tabCount

                val totalWidth = finalTabWidth * tabCount
                val layoutHeight = tabPlaceables.maxOf { it.height }

                layout(totalWidth, layoutHeight) {
                    var xPosition = 0
                    tabPlaceables.forEach { placeable ->
                        val yPosition = (layoutHeight - placeable.height) / 2
                        placeable.placeRelative(x = xPosition, y = yPosition)
                        xPosition += finalTabWidth
                    }
                }
            }
        }
    }
}

@Composable
private fun TabContent(
    tab: NavRoutes.BottomBarTab,
    selected: Boolean,
    enabled: Boolean,
    onTabSelected: (NavRoutes.BottomBarTab) -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.25f
            selected -> 1f
            else -> 0.35f
        },
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected && enabled) 1f else 0.98f,
        visibilityThreshold = 0.000001f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .fillMaxHeight()
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = withHaptic { onTabSelected(tab) }
            )
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val iconRes = if (selected) tab.iconChecked else tab.iconUnchecked
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(tab.title),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f)
        )
        Text(
            text = stringResource(tab.title),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f),
            maxLines = 1,
            modifier = Modifier.basicMarquee(Int.MAX_VALUE)
        )
    }
}
package com.drdisagree.iconify.core.ui.components.scaffolds

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.drdisagree.iconify.R
import com.drdisagree.iconify.core.common.LocalInnerPadding
import com.drdisagree.iconify.core.ui.components.others.showComingSoonToast
import com.drdisagree.iconify.core.ui.components.topappbar.ActionItem
import com.drdisagree.iconify.core.ui.components.topappbar.CollapsingTopAppBar
import com.drdisagree.iconify.core.ui.components.topappbar.TopAppBarAction
import com.drdisagree.iconify.features.common.viewmodels.SystemActionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
    @DrawableRes backIcon: Int? = null,
    showBackIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    showActionIcon: Boolean = true,
    actions: List<TopAppBarAction> = emptyList(),
    content: @Composable (
        innerPadding: PaddingValues,
        scrollBehavior: TopAppBarScrollBehavior
    ) -> Unit,
) {
    val previewMode = LocalInspectionMode.current
    val parentInnerPadding = LocalInnerPadding.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingTopAppBar(
                scrollBehavior = scrollBehavior,
                title = title,
                subtitle = subtitle,
                backIcon = backIcon,
                showBackIcon = showBackIcon,
                onBackClick = onBackClick,
                actions = {
                    (if (previewMode) defaultActions(null)
                    else defaultActions() + actions).forEach { action ->
                        ActionItem(
                            action = action,
                            showActionIcon = showActionIcon
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val adjustedPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + parentInnerPadding.calculateBottomPadding(),
            start = innerPadding.calculateStartPadding(layoutDirection),
            end = innerPadding.calculateEndPadding(layoutDirection)
        )
        content(adjustedPadding, scrollBehavior)
    }
}

@Composable
private fun defaultActions(
    systemActionViewModel: SystemActionViewModel? = hiltViewModel(),
): List<TopAppBarAction> {
    val context = LocalContext.current

    return listOf(
        TopAppBarAction(
            icon = R.drawable.ic_menu,
            label = "Menu",
            subItems = listOf(
                TopAppBarAction(
                    icon = R.drawable.ic_changelog,
                    label = stringResource(R.string.changelog),
                    onClick = { showComingSoonToast(context) }
                ),
                TopAppBarAction(
                    icon = R.drawable.ic_upload_file,
                    label = stringResource(R.string.import_export),
                    subItems = listOf(
                        TopAppBarAction(
                            R.drawable.ic_file_import,
                            stringResource(R.string.import_settings),
                            onClick = { showComingSoonToast(context) }
                        ),
                        TopAppBarAction(
                            R.drawable.ic_file_export,
                            stringResource(R.string.export_settings),
                            onClick = { showComingSoonToast(context) }
                        ),
                    ),
                ),
                TopAppBarAction(
                    icon = R.drawable.ic_xposed_restart_systemui,
                    label = stringResource(R.string.btn_restart_systemui),
                    onClick = { systemActionViewModel?.triggerRestartSystemUI() }
                )
            )
        ),
    )
}
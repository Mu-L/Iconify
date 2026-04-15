package com.drdisagree.iconify.features.home.tweaks.statusbar.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drdisagree.iconify.R
import com.drdisagree.iconify.core.preferences.PrefValue
import com.drdisagree.iconify.core.preferences.PreferenceListener
import com.drdisagree.iconify.core.preferences.PreferenceScreen
import com.drdisagree.iconify.core.preferences.preferenceScreen
import com.drdisagree.iconify.core.preferences.stringRes
import com.drdisagree.iconify.core.ui.components.dialogs.LoadingDialog
import com.drdisagree.iconify.core.ui.components.others.PreviewComposable
import com.drdisagree.iconify.core.ui.components.others.ToastAppliedEvent
import com.drdisagree.iconify.data.keys.TweaksKey
import com.drdisagree.iconify.features.common.viewmodels.SystemActionViewModel
import com.drdisagree.iconify.features.home.tweaks.statusbar.viewmodels.StatusbarViewModel
import kotlin.math.roundToInt

val statusbarPreferences = preferenceScreen {
    category {
        slider(
            key = TweaksKey.STATUSBAR_START_PADDING,
            title = stringRes(R.string.sb_start_padding),
            min = -1f,
            max = 120f,
            valueLabel = { "${it.toInt()}dp" },
            showDefaultIndicator = true,
            hideDefaultValue = true,
            showResetButton = true,
        )

        slider(
            key = TweaksKey.STATUSBAR_END_PADDING,
            title = stringRes(R.string.sb_end_padding),
            min = -1f,
            max = 120f,
            valueLabel = { "${it.toInt()}dp" },
            showDefaultIndicator = true,
            hideDefaultValue = true,
            showResetButton = true,
        )

        slider(
            key = TweaksKey.STATUSBAR_HEIGHT,
            title = stringRes(R.string.sb_height),
            min = -1f,
            max = 240f,
            valueLabel = { "${it.toInt()}dp" },
            showDefaultIndicator = true,
            hideDefaultValue = true,
            showResetButton = true,
        )
    }
}

@Composable
fun TweaksStatusbarScreen(
    statusbarViewModel: StatusbarViewModel = hiltViewModel(),
    systemActionViewModel: SystemActionViewModel = hiltViewModel()
) {
    val isApplying by statusbarViewModel.isLoading.collectAsStateWithLifecycle()

    if (isApplying) {
        LoadingDialog()
    }

    ToastAppliedEvent(statusbarViewModel.uiEvent)

    PreferenceListener { event ->
        when (event.key) {
            TweaksKey.STATUSBAR_START_PADDING.name -> {
                val padding = (event.newValue as PrefValue.FloatValue).v.roundToInt()
                statusbarViewModel.applyStatusbarStartPadding(padding) {
                    systemActionViewModel.shouldRestartSystemUI()
                }
            }

            TweaksKey.STATUSBAR_END_PADDING.name -> {
                val padding = (event.newValue as PrefValue.FloatValue).v.roundToInt()
                statusbarViewModel.applyStatusbarEndPadding(padding) {
                    systemActionViewModel.shouldRestartSystemUI()
                }
            }

            TweaksKey.STATUSBAR_HEIGHT.name -> {
                val height = (event.newValue as PrefValue.FloatValue).v.roundToInt()
                statusbarViewModel.applyStatusbarHeight(height) {
                    systemActionViewModel.shouldRestartSystemUI()
                }
            }
        }
    }

    StatusbarScreenContent()
}

@Composable
private fun StatusbarScreenContent() {
    PreferenceScreen(
        items = statusbarPreferences,
        title = stringResource(R.string.activity_title_statusbar),
        showBackIcon = true
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusbarScreenPreview() {
    PreviewComposable {
        StatusbarScreenContent()
    }
}
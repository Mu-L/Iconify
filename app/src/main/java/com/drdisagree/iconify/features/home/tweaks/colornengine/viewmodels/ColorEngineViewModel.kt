package com.drdisagree.iconify.features.home.tweaks.colornengine.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.iconify.core.preferences.PreferenceController
import com.drdisagree.iconify.core.utils.overlay.OverlayUtils
import com.drdisagree.iconify.core.utils.overlay.resource.ResourceEntry
import com.drdisagree.iconify.core.utils.overlay.resource.ResourceManager
import com.drdisagree.iconify.data.common.Const.FRAMEWORK_PACKAGE
import com.drdisagree.iconify.data.events.ToastUiEvent
import com.drdisagree.iconify.data.keys.TweaksKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColorEngineViewModel @Inject constructor(
    private val prefController: PreferenceController
) : ViewModel() {

    private val tag = "ColorEngineViewModel"

    private val basicPrimaryColorId = "basic_primary_color"
    private val basicSecondaryColorId = "basic_primary_color"

    private fun basicPrimaryColorResourceEntries(hex: String) = listOf(
        ResourceEntry(
            FRAMEWORK_PACKAGE,
            "color",
            "holo_blue_light",
            hex
        ),
        ResourceEntry(
            FRAMEWORK_PACKAGE,
            "color",
            "holo_green_light",
            hex
        )
    )

    private fun basicSecondaryColorResourceEntries(hex: String) = listOf(
        ResourceEntry(
            FRAMEWORK_PACKAGE,
            "color",
            "holo_blue_dark",
            hex
        ),
        ResourceEntry(
            FRAMEWORK_PACKAGE,
            "color",
            "holo_green_dark",
            hex
        )
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ToastUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun applyPrimaryColor(hex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isLoading.value) return@launch

            _isLoading.value = true

            val entries = basicPrimaryColorResourceEntries(hex)

            val error = ResourceManager.buildOverlayWithResource(
                basicPrimaryColorId,
                *entries.toTypedArray()
            )

            _isLoading.value = false

            if (!error) {
                _uiEvent.emit(ToastUiEvent.Applied)
            } else {
                _uiEvent.emit(ToastUiEvent.Error)
            }
        }
    }

    fun applySecondaryColor(hex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isLoading.value) return@launch

            _isLoading.value = true

            val entries = basicSecondaryColorResourceEntries(hex)

            val error = ResourceManager.buildOverlayWithResource(
                basicSecondaryColorId,
                *entries.toTypedArray()
            )

            _isLoading.value = false

            if (!error) {
                _uiEvent.emit(ToastUiEvent.Applied)
            } else {
                _uiEvent.emit(ToastUiEvent.Error)
            }
        }
    }

    fun toggleMonetAccent(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isLoading.value) return@launch

            _isLoading.value = true
            delay(500)

            val monetGradientEnabled = prefController.getBoolean(TweaksKey.MONET_GRADIENT)

            if (enable) {
                if (!monetGradientEnabled) {
                    disableBasicColors()
                }
                OverlayUtils.changeOverlayState(
                    "IconifyComponentAMGC.overlay", false,
                    "IconifyComponentAMAC.overlay", true,
                )
            } else {
                OverlayUtils.disableOverlay("IconifyComponentAMAC.overlay")
                if (!monetGradientEnabled) {
                    applyBasicColors()
                }
            }

            delay(500)
            _isLoading.value = false

            _uiEvent.emit(ToastUiEvent.Applied)
        }
    }

    fun toggleMonetGradient(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isLoading.value) return@launch

            _isLoading.value = true
            delay(500)

            val monetAccentEnabled = prefController.getBoolean(TweaksKey.MONET_ACCENT)

            if (enable) {
                if (!monetAccentEnabled) {
                    disableBasicColors()
                }
                OverlayUtils.changeOverlayState(
                    "IconifyComponentAMAC.overlay", false,
                    "IconifyComponentAMGC.overlay", true,
                )
            } else {
                OverlayUtils.disableOverlay("IconifyComponentAMGC.overlay")
                if (!monetAccentEnabled) {
                    applyBasicColors()
                }
            }

            delay(500)
            _isLoading.value = false

            _uiEvent.emit(ToastUiEvent.Applied)
        }
    }

    private suspend fun applyBasicColors() {
        val primaryColor = prefController.getString(TweaksKey.BASIC_COLOR_PRIMARY)
        val secondaryColor = prefController.getString(TweaksKey.BASIC_COLOR_SECONDARY)

        val primaryColorEntries = basicPrimaryColorResourceEntries(primaryColor)
        val secondaryColorEntries = basicPrimaryColorResourceEntries(secondaryColor)

        val error = try {
            ResourceManager.insertResources(
                overlayId = basicPrimaryColorId,
                resourceEntries = primaryColorEntries.toTypedArray()
            )
            ResourceManager.insertResources(
                overlayId = basicSecondaryColorId,
                resourceEntries = secondaryColorEntries.toTypedArray()
            )
            ResourceManager.triggerDynamicOverlayUpdate(
                packagesToUpdate = (primaryColorEntries + secondaryColorEntries)
                    .map { it.packageName }
                    .distinct()
            )
        } catch (e: Exception) {
            Log.e(tag, "applyBasicColors", e)
            true
        }

        if (error) {
            _uiEvent.emit(ToastUiEvent.Error)
        }
    }

    private suspend fun disableBasicColors() {
        val primaryColorEntries = basicPrimaryColorResourceEntries("#FFFFFF")
        val secondaryColorEntries = basicPrimaryColorResourceEntries("#FFFFFF")

        val error = ResourceManager.removeResourceFromOverlay(
            overlayIds = listOf(basicPrimaryColorId, basicSecondaryColorId),
            packagesToUpdate = (primaryColorEntries + secondaryColorEntries)
                .map { it.packageName }
                .distinct()
        )

        if (error) {
            _uiEvent.emit(ToastUiEvent.Error)
        }
    }
}
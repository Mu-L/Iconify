package com.drdisagree.iconify.core.ui.components.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drdisagree.iconify.core.preferences.PreferenceController
import com.drdisagree.iconify.core.preferences.PreferenceDefinition
import com.drdisagree.iconify.core.preferences.PreferenceType

@Composable
fun PreferenceItem(
    prefDefinition: PreferenceDefinition,
    prefController: PreferenceController,
    shape: RoundedCornerShape,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    when (val type = prefDefinition.type) {
        is PreferenceType.Custom -> {
            Box(modifier = modifier) {
                type.content()
            }
        }

        is PreferenceType.Switch -> SwitchPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.Slider -> SliderPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.ListPref -> ListPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.MultiList -> MultiListPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.Action -> ActionPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.EditText -> EditTextPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            modifier = modifier
        )

        is PreferenceType.TwoTargetSwitch -> TwoTargetSwitchPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier,
        )

        is PreferenceType.ColorPicker -> ColorPickerPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.FilePicker -> FilePickerPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            shape = shape,
            isEnabled = isEnabled,
            type = type,
            modifier = modifier
        )

        is PreferenceType.Info -> InfoPreferenceItem(
            prefDefinition = prefDefinition,
            prefController = prefController,
            isEnabled = isEnabled,
            modifier = modifier
        )
    }
}
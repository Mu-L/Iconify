package com.drdisagree.iconify.data.keys

enum class TweaksKey(override val default: Any?) : Key {
    // Statusbar
    STATUSBAR_START_PADDING(-1f),
    STATUSBAR_END_PADDING(-1f),
    STATUSBAR_HEIGHT(-1f),

    // Miscellaneous
    TABLET_LANDSCAPE(false),
    NOTCH_BAR_KILLER(false),
    TABLET_HEADER(false),
    ACCENT_PRIVACY_CHIP(false),
    DISABLE_PROGRESS_WAVE(false)
}
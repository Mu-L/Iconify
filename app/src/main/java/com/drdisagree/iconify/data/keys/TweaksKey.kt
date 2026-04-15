package com.drdisagree.iconify.data.keys

enum class TweaksKey(override val default: Any?) : Key {
    // Color Engine
    BASIC_COLOR_PRIMARY("#FF0000"),
    BASIC_COLOR_SECONDARY("#0000FF"),
    MONET_ACCENT(false),
    MONET_GRADIENT(true),

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
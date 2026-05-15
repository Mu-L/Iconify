package com.drdisagree.iconify.core.search

import com.drdisagree.iconify.R
import com.drdisagree.iconify.app.navigation.NavRoutes
import com.drdisagree.iconify.core.preferences.PrefStringRes
import com.drdisagree.iconify.core.preferences.PreferenceDefinition
import com.drdisagree.iconify.core.preferences.PreferenceScreenItem
import com.drdisagree.iconify.core.preferences.PreferenceType
import com.drdisagree.iconify.core.preferences.stringRes

/**
 * Static registry of all searchable preference items across the entire app.
 *
 * Each entry maps a [PreferenceDefinition] to the [NavRoutes] that hosts it,
 * along with breadcrumb labels so the search UI can show the navigation path.
 *
 * Only preferences with a meaningful title are indexed (Custom / Composable items
 * and Info items are excluded since they don't represent interactive settings).
 */
object SearchIndex {

    /**
     * Describes one screen's worth of preferences for indexing.
     */
    private data class ScreenEntry(
        val items: List<PreferenceScreenItem>,
        val screenTitleResId: Int,
        val breadcrumbs: List<PrefStringRes>,
        val route: NavRoutes,
    )

    val allItems: List<SearchablePreference> by lazy { buildIndex() }

    private fun buildIndex(): List<SearchablePreference> {
        val entries = allScreenEntries()
        return entries.flatMap { entry ->
            entry.items
                .filterIsInstance<PreferenceScreenItem.Category>()
                .flatMap { it.definition.preferences }
                .filter { pref -> isSearchable(pref) }
                .map { pref ->
                    SearchablePreference(
                        key = pref.key,
                        title = pref.title,
                        summary = null, // summary is lambda-based, can't extract statically
                        screenTitleResId = entry.screenTitleResId,
                        breadcrumbs = entry.breadcrumbs,
                        route = entry.route,
                        isVisible = pref.isVisible,
                    )
                }
        }
    }

    /**
     * Returns true if the preference should appear in search.
     * We exclude Custom composables, Info items, and items with empty titles.
     */
    private fun isSearchable(pref: PreferenceDefinition): Boolean {
        if (pref.type is PreferenceType.Custom) return false
        if (pref.type is PreferenceType.Info) return false
        val title = pref.title
        return !(title is PrefStringRes.Hardcoded && title.value.isBlank())
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Screen Registry
    //
    //  Each screen that uses `preferenceScreen { }` is registered here with
    //  its breadcrumbs and NavRoutes destination.
    // ────────────────────────────────────────────────────────────────────────

    private fun allScreenEntries(): List<ScreenEntry> {
        val xposed = stringRes(R.string.navbar_xposed)
        val home = stringRes(R.string.navbar_home)
        val settings = stringRes(R.string.navbar_settings)
        val tweaks = stringRes(R.string.navbar_tweaks)
        val statusbar = stringRes(R.string.activity_title_statusbar)
        val quickSettings = stringRes(R.string.activity_title_quick_settings)
        val lockscreen = stringRes(R.string.activity_title_lockscreen)

        return listOf(
            // ── Settings ──────────────────────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.settings.main.screens
                    .settingsPreferences(),
                screenTitleResId = R.string.activity_title_settings,
                breadcrumbs = listOf(settings),
                route = NavRoutes.MainGraph.Settings.Tab,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.settings.lookandfeel.screens
                    .lookAndFeelPreferences,
                screenTitleResId = R.string.look_and_feel_title,
                breadcrumbs = listOf(
                    settings,
                    stringRes(R.string.look_and_feel_title)
                ),
                route = NavRoutes.MainGraph.Settings.LookAndFeel,
            ),

            // ── Xposed > Status Bar ───────────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.statusbar.main.screens
                    .statusbarPreferences,
                screenTitleResId = R.string.activity_title_statusbar,
                breadcrumbs = listOf(xposed, statusbar),
                route = NavRoutes.MainGraph.Xposed.Statusbar.Main,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.statusbar.clockchip.screens
                    .clockChipPreferences,
                screenTitleResId = R.string.activity_title_background_chip,
                breadcrumbs = listOf(
                    xposed, statusbar,
                    stringRes(R.string.activity_title_background_chip)
                ),
                route = NavRoutes.MainGraph.Xposed.Statusbar.ClockChip,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.statusbar.batterystyle.screens
                    .batteryStylePreferences(),
                screenTitleResId = R.string.activity_title_battery_style,
                breadcrumbs = listOf(
                    xposed, statusbar,
                    stringRes(R.string.activity_title_battery_style)
                ),
                route = NavRoutes.MainGraph.Xposed.Statusbar.BatteryStyle,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.statusbar.logo.screens
                    .statusbarLogoPreferences(),
                screenTitleResId = R.string.status_bar_logo_title,
                breadcrumbs = listOf(
                    xposed, statusbar,
                    stringRes(R.string.status_bar_logo_title)
                ),
                route = NavRoutes.MainGraph.Xposed.Statusbar.Logo,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.statusbar.dualstatusbar.screens
                    .dualStatusbarPreferences,
                screenTitleResId = R.string.dual_status_bar_title,
                breadcrumbs = listOf(
                    xposed, statusbar,
                    stringRes(R.string.dual_status_bar_title)
                ),
                route = NavRoutes.MainGraph.Xposed.Statusbar.DualStatusbar,
            ),

            // ── Xposed > Quick Settings ───────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.main.screens
                    .quickSettingsPreferences,
                screenTitleResId = R.string.activity_title_quick_settings,
                breadcrumbs = listOf(xposed, quickSettings),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Main,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.transparency.screens
                    .qsTransparencyPreferences,
                screenTitleResId = R.string.activity_title_transparency_blur,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_transparency_blur)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Transparency,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.headerimage.screens
                    .headerImagePreferences,
                screenTitleResId = R.string.activity_title_header_image,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_header_image)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.HeaderImage,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.clock.screens
                    .headerClockPreferences,
                screenTitleResId = R.string.activity_title_header_clock,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_header_clock)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Clock,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.grid.screens
                    .qsGridPreferences,
                screenTitleResId = R.string.activity_title_qs_row_column,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_qs_row_column)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Grid,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.themes.screens
                    .qsThemesPreferences,
                screenTitleResId = R.string.activity_title_themes,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_themes)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Themes,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.quicksettings.margins.screens
                    .qsMarginsPreferences,
                screenTitleResId = R.string.activity_title_qs_panel_margin,
                breadcrumbs = listOf(
                    xposed, quickSettings,
                    stringRes(R.string.activity_title_qs_panel_margin)
                ),
                route = NavRoutes.MainGraph.Xposed.QuickSettings.Margins,
            ),

            // ── Xposed > Lockscreen ───────────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.lockscreen.main.screens
                    .lockscreenPreferences,
                screenTitleResId = R.string.activity_title_lockscreen,
                breadcrumbs = listOf(xposed, lockscreen),
                route = NavRoutes.MainGraph.Xposed.Lockscreen.Main,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.lockscreen.clock.screens
                    .lsClockPreferences,
                screenTitleResId = R.string.activity_title_lockscreen_clock,
                breadcrumbs = listOf(
                    xposed, lockscreen,
                    stringRes(R.string.activity_title_lockscreen_clock)
                ),
                route = NavRoutes.MainGraph.Xposed.Lockscreen.Clock,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.lockscreen.albumart.screens
                    .lsAlbumArtPreferences,
                screenTitleResId = R.string.activity_title_lockscreen_album_art,
                breadcrumbs = listOf(
                    xposed, lockscreen,
                    stringRes(R.string.activity_title_lockscreen_album_art)
                ),
                route = NavRoutes.MainGraph.Xposed.Lockscreen.MediaAlbumArt,
            ),

            // ── Xposed > Volume Panel ─────────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.xposed.volumepanel.screens
                    .volumePanelPreferences,
                screenTitleResId = R.string.activity_title_volume_panel,
                breadcrumbs = listOf(
                    xposed,
                    stringRes(R.string.activity_title_volume_panel)
                ),
                route = NavRoutes.MainGraph.Xposed.VolumePanel,
            ),

            // ── Home > Tweaks > Status Bar ────────────────────────────────
            ScreenEntry(
                items = com.drdisagree.iconify.features.home.tweaks.statusbar.screens
                    .statusbarPreferences,
                screenTitleResId = R.string.activity_title_statusbar,
                breadcrumbs = listOf(home, tweaks, statusbar),
                route = NavRoutes.MainGraph.Home.More.StatusBar,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.home.tweaks.cornerradius.screens
                    .cornerRadiusPreferences,
                screenTitleResId = R.string.activity_title_ui_roundness,
                breadcrumbs = listOf(
                    home, tweaks,
                    stringRes(R.string.activity_title_ui_roundness)
                ),
                route = NavRoutes.MainGraph.Home.More.UIRoundness,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.home.tweaks.navigationbar.screens
                    .navigationbarPreferences,
                screenTitleResId = R.string.activity_title_navigation_bar,
                breadcrumbs = listOf(
                    home, tweaks,
                    stringRes(R.string.activity_title_navigation_bar)
                ),
                route = NavRoutes.MainGraph.Home.More.NavigationBar,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.home.tweaks.mediaplayer.screens
                    .mediaPlayerPreferences,
                screenTitleResId = R.string.activity_title_media_player,
                breadcrumbs = listOf(
                    home, tweaks,
                    stringRes(R.string.activity_title_media_player)
                ),
                route = NavRoutes.MainGraph.Home.More.MediaPlayer,
            ),
            ScreenEntry(
                items = com.drdisagree.iconify.features.home.tweaks.miscellaneous.screens
                    .miscellaneousPreferences,
                screenTitleResId = R.string.activity_title_miscellaneous,
                breadcrumbs = listOf(
                    home, tweaks,
                    stringRes(R.string.activity_title_miscellaneous)
                ),
                route = NavRoutes.MainGraph.Home.More.Miscellaneous,
            ),
        )
    }
}

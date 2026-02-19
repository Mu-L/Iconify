package com.drdisagree.iconify.xposed

import android.os.Build
import com.drdisagree.iconify.data.common.Const.SETTINGS_PACKAGE
import com.drdisagree.iconify.data.common.Const.SYSTEMUI_PACKAGE
import com.drdisagree.iconify.xposed.modules.BackgroundChip
import com.drdisagree.iconify.xposed.modules.extras.callbacks.ConfigurationCallback
import com.drdisagree.iconify.xposed.modules.extras.callbacks.ControllersProvider
import com.drdisagree.iconify.xposed.modules.extras.callbacks.DozeCallback
import com.drdisagree.iconify.xposed.modules.extras.callbacks.HeadsUpCallback
import com.drdisagree.iconify.xposed.modules.extras.callbacks.KeyguardShowingCallback
import com.drdisagree.iconify.xposed.modules.extras.callbacks.QsShowingCallback
import com.drdisagree.iconify.xposed.modules.extras.callbacks.ThemeChangeCallback
import com.drdisagree.iconify.xposed.modules.extras.utils.MyConstraintSet
import com.drdisagree.iconify.xposed.modules.extras.utils.SettingsLibUtils
import com.drdisagree.iconify.xposed.modules.lockscreen.AlbumArt
import com.drdisagree.iconify.xposed.modules.lockscreen.Lockscreen
import com.drdisagree.iconify.xposed.modules.lockscreen.clock.LockscreenClock
import com.drdisagree.iconify.xposed.modules.lockscreen.clock.LockscreenClockA15
import com.drdisagree.iconify.xposed.modules.lockscreen.depthwallpaper.DepthWallpaperA15
import com.drdisagree.iconify.xposed.modules.lockscreen.weather.LockscreenWeather
import com.drdisagree.iconify.xposed.modules.lockscreen.weather.LockscreenWeatherA15
import com.drdisagree.iconify.xposed.modules.lockscreen.widgets.LockscreenWidgets
import com.drdisagree.iconify.xposed.modules.lockscreen.widgets.LockscreenWidgetsA15
import com.drdisagree.iconify.xposed.modules.misc.Miscellaneous
import com.drdisagree.iconify.xposed.modules.quicksettings.AppIconInNotification
import com.drdisagree.iconify.xposed.modules.quicksettings.ColorizeNotificationView
import com.drdisagree.iconify.xposed.modules.quicksettings.HeaderImage
import com.drdisagree.iconify.xposed.modules.quicksettings.HeadsUpBlur
import com.drdisagree.iconify.xposed.modules.quicksettings.QSTransparency
import com.drdisagree.iconify.xposed.modules.quicksettings.QuickSettings
import com.drdisagree.iconify.xposed.modules.quicksettings.headerclock.HeaderClockA14
import com.drdisagree.iconify.xposed.modules.statusbar.AppIconsInStatusbar
import com.drdisagree.iconify.xposed.modules.statusbar.DualStatusbar
import com.drdisagree.iconify.xposed.modules.statusbar.OnGoingActionChip
import com.drdisagree.iconify.xposed.modules.statusbar.StatusbarLogo
import com.drdisagree.iconify.xposed.modules.statusbar.StatusbarMisc
import com.drdisagree.iconify.xposed.modules.statusbar.SwapSignalNetworkType
import com.drdisagree.iconify.xposed.modules.statusbar.SwapWiFiCellular
import com.drdisagree.iconify.xposed.modules.volume.VolumePanel
import com.drdisagree.iconify.xposed.utils.HookCheck

object EntryList {

    private val topPriorityCommonModPacks: List<Class<out ModPack>> = listOf(
        SettingsLibUtils::class.java,
        HookCheck::class.java
    )

    private val systemUICommonModPacks: List<Class<out ModPack>> = listOf(
        MyConstraintSet::class.java,
        ControllersProvider::class.java,
        ThemeChangeCallback::class.java,
        HeadsUpCallback::class.java,
        QsShowingCallback::class.java,
        KeyguardShowingCallback::class.java,
        DozeCallback::class.java,
        ConfigurationCallback::class.java,
        BackgroundChip::class.java,
        HeaderImage::class.java,
        Lockscreen::class.java,
        LockscreenClock::class.java,
        LockscreenWidgets::class.java,
        LockscreenWeather::class.java,
        AlbumArt::class.java,
        Miscellaneous::class.java,
        QSTransparency::class.java,
        QuickSettings::class.java,
        AppIconsInStatusbar::class.java,
        SwapWiFiCellular::class.java,
        SwapSignalNetworkType::class.java,
        DualStatusbar::class.java,
        StatusbarMisc::class.java,
        VolumePanel::class.java,
        ColorizeNotificationView::class.java,
        AppIconInNotification::class.java,
        HeadsUpBlur::class.java,
        OnGoingActionChip::class.java,
        StatusbarLogo::class.java
    )

    private val systemUiAndroid16ModPacks: List<Class<out ModPack>> = listOf(
        DepthWallpaperA15::class.java,
        HeaderClockA14::class.java,
        LockscreenClockA15::class.java,
        LockscreenWeatherA15::class.java,
        LockscreenWidgetsA15::class.java
    )

    fun getEntries(packageName: String): ArrayList<Class<out ModPack>> {
        val modPacks = ArrayList<Class<out ModPack>>()

        modPacks.addAll(topPriorityCommonModPacks)

        when (packageName) {
            SYSTEMUI_PACKAGE -> {
                if (!HookEntry.isChildProcess) {
                    modPacks.addAll(systemUICommonModPacks)

                    when {
                        Build.VERSION.SDK_INT >= 36 -> { // android 16+
                            modPacks.addAll(systemUiAndroid16ModPacks)
                        }
                    }
                }
            }

            SETTINGS_PACKAGE -> {
                //                modPacks.addAll(settingsCommonModPacks)
                //
                //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                //                    modPacks.addAll(settingsAndroid15ModPacks)
                //                }
            }
        }

        return modPacks
    }
}

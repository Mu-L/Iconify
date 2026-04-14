package com.drdisagree.iconify.core.utils.overlay.resource

import android.util.Log
import com.drdisagree.iconify.app.Iconify.Companion.appContext
import com.drdisagree.iconify.core.di.ResourceManagerEntryPoint
import com.drdisagree.iconify.core.utils.overlay.compilers.DynamicCompiler
import com.drdisagree.iconify.data.common.Const.DYNAMIC_OVERLAYABLE_PACKAGES
import com.drdisagree.iconify.data.entity.DynamicResourceEntity
import com.drdisagree.iconify.data.repository.DynamicResourceRepository
import dagger.hilt.android.EntryPointAccessors

object ResourceManager {

    private const val TAG = "ResourceManager"

    private fun getEntryPoint(): ResourceManagerEntryPoint {
        return EntryPointAccessors.fromApplication(
            appContext,
            ResourceManagerEntryPoint::class.java
        )
    }

    private fun repository(): DynamicResourceRepository {
        return getEntryPoint().repository()
    }

    suspend fun buildOverlayWithResource(
        overlayId: String,
        vararg resourceEntries: ResourceEntry?
    ): Boolean {
        require(resourceEntries.isNotEmpty()) { "No resource entries provided" }

        return try {
            val entries = resourceEntries.filterNotNull()

            repository().insertResources(
                entries.map { entry ->
                    DynamicResourceEntity(
                        overlayId = overlayId,
                        packageName = entry.packageName,
                        startEndTag = entry.startEndTag,
                        resourceName = entry.resourceName,
                        resourceValue = entry.resourceValue,
                        isPortrait = entry.isPortrait,
                        isLandscape = entry.isLandscape,
                        isNightMode = entry.isNightMode
                    )
                }
            )

            DynamicCompiler.buildDynamicOverlay(
                packagesToUpdate = entries
                    .map { it.packageName }
                    .distinct()
            )
        } catch (e: Exception) {
            Log.e(TAG, "buildOverlayWithResource", e)
            true
        }
    }

    suspend fun removeResourceFromOverlay(
        overlayIds: List<String>,
        packagesToUpdate: List<String>
    ): Boolean {
        require(overlayIds.isNotEmpty()) { "No overlay IDs provided" }
        require(packagesToUpdate.isNotEmpty()) { "No packages provided" }

        return try {
            repository().deleteOverlays(*overlayIds.toTypedArray())

            DynamicCompiler.buildDynamicOverlay(packagesToUpdate = packagesToUpdate)
        } catch (e: Exception) {
            Log.e(TAG, "removeResourceFromOverlay", e)
            true
        }
    }

    suspend fun generateXmlStructureForAllResources(
        packagesToUpdate: List<String>
    ): Map<String, Map<ResourceType, List<String>>> {
        require(packagesToUpdate.isNotEmpty()) { "No packages provided" }

        val all = repository().getAllLatestResources()
        val grouped = all.groupBy { it.packageName }
        val emptyResources = getEmptyResources()

        val result = mutableMapOf<String, MutableMap<ResourceType, List<String>>>()

        DYNAMIC_OVERLAYABLE_PACKAGES.forEach { packageName ->
            if (packageName !in packagesToUpdate) return@forEach

            val resources = grouped[packageName].orEmpty()
            val xmlMap = mutableMapOf<ResourceType, List<String>>()

            val typeGrouped = resources.groupBy {
                when {
                    it.isPortrait -> ResourceType.PORTRAIT
                    it.isLandscape -> ResourceType.LANDSCAPE
                    it.isNightMode -> ResourceType.NIGHT
                    else -> throw Exception("Invalid type")
                }
            }

            ResourceType.entries.forEach { type ->
                val group = typeGrouped[type].orEmpty()

                val xml = if (group.isEmpty() && type == ResourceType.PORTRAIT) {
                    emptyResources[packageName]!!
                } else {
                    buildList {
                        add("""<?xml version="1.0" encoding="utf-8"?>""")
                        add("<resources>")
                        group.forEach {
                            add(
                                "<${it.startEndTag} name=\"${it.resourceName}\">${it.resourceValue}</${it.startEndTag}>"
                            )
                        }
                        add("</resources>")
                    }
                }

                xmlMap[type] = xml
            }

            result[packageName] = xmlMap
        }

        return result
    }

    private fun getEmptyResources(): Map<String, List<String>> {
        return DYNAMIC_OVERLAYABLE_PACKAGES.mapIndexed { index, pkg ->
            pkg to listOf(
                """<?xml version="1.0" encoding="utf-8"?>""",
                "<resources>",
                "<color name=\"dummy${index + 1}\">#00000000</color>",
                "</resources>"
            )
        }.toMap()
    }
}
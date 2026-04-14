package com.drdisagree.iconify.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.drdisagree.iconify.data.common.Resources.DYNAMIC_RESOURCE_TABLE

@Entity(
    tableName = DYNAMIC_RESOURCE_TABLE,
    indices = [
        Index("packageName"),
        Index("overlayId"),
        Index("resourceName"),
        Index("createdAt"),
        Index(
            value = [
                "packageName",
                "resourceName",
                "startEndTag",
                "isPortrait",
                "isLandscape",
                "isNightMode"
            ]
        )
    ]
)
data class DynamicResourceEntity(
    @PrimaryKey
    val overlayId: String,
    val packageName: String,
    val startEndTag: String,
    val resourceName: String,
    val resourceValue: String,
    val isPortrait: Boolean,
    val isLandscape: Boolean,
    val isNightMode: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
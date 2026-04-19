package com.drdisagree.iconify.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.drdisagree.iconify.app.Iconify.Companion.appContext
import com.drdisagree.iconify.data.common.Resources.DYNAMIC_RESOURCE_DATABASE_NAME
import com.drdisagree.iconify.data.dao.DynamicResourceDao
import com.drdisagree.iconify.data.dao.FabricatedResourceDao
import com.drdisagree.iconify.data.entity.DynamicResourceEntity
import com.drdisagree.iconify.data.entity.FabricatedResourceEntity

@Database(
    entities = [
        DynamicResourceEntity::class,
        FabricatedResourceEntity::class], version = 2
)
abstract class ResourceDatabase : RoomDatabase() {

    abstract fun dynamicResourceDao(): DynamicResourceDao

    abstract fun fabricatedResourceDao(): FabricatedResourceDao

    companion object {
        @Volatile
        private var INSTANCE: ResourceDatabase? = null

        fun getInstance(): ResourceDatabase {
            return INSTANCE ?: synchronized(this) {
                Room
                    .databaseBuilder(
                    appContext,
                        ResourceDatabase::class.java,
                        DYNAMIC_RESOURCE_DATABASE_NAME
                    ).fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
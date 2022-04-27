package com.mtirado.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(RouteEntity::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoutesDatabase : RoomDatabase() {
    abstract fun routesDao(): RouteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RoutesDatabase? = null

        fun getDatabase(context: Context): RoutesDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutesDatabase::class.java,
                    "routes_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
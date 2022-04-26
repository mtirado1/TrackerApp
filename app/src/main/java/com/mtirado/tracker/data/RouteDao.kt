package com.mtirado.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id=:id ")
    fun getRouteById(id: String): Flow<RouteEntity?>

    @Insert
    fun insert(route: RouteEntity)

    @Query("DELETE  FROM routes where id=:id")
    fun deleteRoute(id: String)
}
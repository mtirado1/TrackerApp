package com.mtirado.tracker.data

import androidx.room.*
import com.mtirado.tracker.domain.route.Path

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val path: Path,
    val description: String?
)





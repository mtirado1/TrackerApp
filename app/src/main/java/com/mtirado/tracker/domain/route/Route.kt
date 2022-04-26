package com.mtirado.tracker.domain.route

import java.util.*

class Route(
    val id: String,
    val name: String,
    val path: Path = Path(),
    val description: String? = null
) {
    val lastPosition: Coordinates? get() = path.points.lastOrNull()

    companion object {
        public fun create(name: String): Route = Route(UUID.randomUUID().toString(), name)
    }
}
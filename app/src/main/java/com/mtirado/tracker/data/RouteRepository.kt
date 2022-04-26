package com.mtirado.tracker.data

import androidx.annotation.WorkerThread
import com.mtirado.tracker.domain.route.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteRepository(private val routeDao: RouteDao) {
    val allRoutes: Flow<List<Route>> = routeDao.getAllRoutes().map { list -> list.map { entityToRoute(it) } }

    fun getRouteById(id: String): Flow<Route?> {
        return routeDao.getRouteById(id).map { entity ->
            if (entity != null) {
                entityToRoute(entity!!)
            } else { null }
        }
    }

    fun insert(route: Route) {
        routeDao.insert(routeToEntity(route))
    }

    companion object {
        fun routeToEntity(route: Route): RouteEntity {
            return RouteEntity(
                id = route.id,
                name = route.name,
                path = route.path,
                description = route.description
            )
        }

        fun entityToRoute(entity: RouteEntity): Route {
            return Route(id = entity.id, name = entity.name, path = entity.path, description = entity.description)
        }
    }
}
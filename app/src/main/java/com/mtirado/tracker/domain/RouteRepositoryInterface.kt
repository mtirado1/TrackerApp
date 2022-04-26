package com.mtirado.tracker.domain

import com.mtirado.tracker.domain.route.Route

interface RouteRepositoryInterface {
    fun getAllRoutes(): List<Route>
    fun getRouteById(id: String): Route?
}
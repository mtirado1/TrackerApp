package com.mtirado.tracker.fragments

import com.mtirado.tracker.domain.route.Route
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.databinding.FragmentRouteMonitorBinding
import com.mtirado.tracker.domain.RouteMonitor
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.SpeedUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter
import com.mtirado.tracker.domain.formatters.UnitsFormatter

class RouteMonitorFragment: Fragment() {
    private var _binding: FragmentRouteMonitorBinding? = null
    private val binding get() = _binding!!

    private lateinit var routeMonitor: RouteMonitor

    private val unitFormatter = UnitsFormatter()
    private val timeFormatter = TimeFormatter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteMonitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        routeMonitor = (activity as MainActivity).monitor
        routeMonitor.routeObservable.subscribe(this::updateDetails)
        binding.runningTime.text = "--:--:--"

        binding.buttonStop.setOnClickListener { routeMonitor.stop() }
        binding.buttonResume.setOnClickListener { routeMonitor.resume() }
        binding.buttonEnd.setOnClickListener {
            routeMonitor.end()?.let { route ->
                val repository = (activity as MainActivity).repository
                repository.insert(route)
                findNavController().navigate(RouteMonitorFragmentDirections.endRoute())
            }
        }

        arguments?.let {
            val routeName = it.getString("routeName") ?: "New Route"
            val logInterval = it.getInt("logInterval")

            if (!routeMonitor.isRunning) {
                routeMonitor.start(Route.create(routeName), logInterval)
            }
        }
    }

    private fun updateDetails(route: Route) {
        with(binding) {
            routeName.text = route.name
            runningTime.text = timeFormatter.format(route.path.duration)
            status.text = "STATUS: ${if (routeMonitor.isRunning) "RUNNING" else "STOPPED"}"

            distance.text = unitFormatter.format(route.path.distance, DistanceUnits.KILOMETERS)
            coordinates.text = route.lastPosition?.toString() ?: "- -"
            elevation.text = unitFormatter.format(route.lastPosition?.altitude ?: 0.0, DistanceUnits.METERS)

            instantSpeed.text = formatSpeed(route.path.instantSpeed)
            averageSpeed.text = formatSpeed(route.path.speed)
        }
    }

    private fun formatSpeed(speed: Double): String {
        if (speed.isNaN()) {
            return "- -"
        }
        return unitFormatter.format(speed, SpeedUnits.KILOMETERS_PER_HOUR)
    }
}
package com.mtirado.tracker.fragments

import com.mtirado.tracker.domain.route.Route
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.R
import com.mtirado.tracker.databinding.FragmentRouteMonitorBinding
import com.mtirado.tracker.domain.RouteMonitor
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.SpeedUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter
import com.mtirado.tracker.domain.formatters.UnitsFormatter
import io.reactivex.disposables.Disposable

class RouteMonitorFragment: Fragment() {
    private var _binding: FragmentRouteMonitorBinding? = null
    private val binding get() = _binding!!

    private lateinit var routeMonitor: RouteMonitor
    private lateinit var subscription: Disposable

    private val unitFormatter = UnitsFormatter()
    private val timeFormatter = TimeFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(RouteMonitorFragmentDirections.endRoute())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteMonitorBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        routeMonitor = (activity as MainActivity).monitor
        subscription = routeMonitor.routeObservable.subscribe(this::updateDetails)
        binding.runningTime.text = "--:--:--"

        showStopButton()
        binding.buttonStop.setOnClickListener {
            showEndButton()
            routeMonitor.stop()
        }

        binding.buttonResume.setOnClickListener {
            showStopButton()
            routeMonitor.resume()
        }

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
            val currentlyActive = it.getBoolean("active")
            if (!currentlyActive) {
                val newRoute = Route.create(routeName)
                updateDetails(newRoute)
                routeMonitor.start(newRoute, logInterval)
            } else if (routeMonitor.last != null) {
                updateDetails(routeMonitor.last!!)
            }
        }
    }

    private fun updateDetails(route: Route) {
        with(binding) {
            routeName.text = route.name
            runningTime.text = timeFormatter.format(route.path.duration)
            status.text = "STATUS: ${if (routeMonitor.isRunning) "RUNNING" else "STOPPED"}"

            distance.text = unitFormatter.format(route.path.distance, DistanceUnits.KILOMETERS)
            latitude.text = route.lastPosition?.latitudeString ?: "- -"
            longitude.text = route.lastPosition?.longitudeString ?: "- -"
            elevation.text = unitFormatter.format(route.lastPosition?.altitude ?: 0.0, DistanceUnits.METERS)

            instantSpeed.text = formatSpeed(route.path.instantSpeed)
            averageSpeed.text = formatSpeed(route.path.speed)
        }
    }

    private fun showStopButton() {
        binding.buttonStop.visibility = View.VISIBLE
        binding.buttonResume.visibility = View.GONE
        binding.buttonEnd.visibility = View.GONE
    }

    private fun showEndButton() {
        binding.buttonStop.visibility = View.GONE
        binding.buttonResume.visibility = View.VISIBLE
        binding.buttonEnd.visibility = View.VISIBLE
    }

    private fun formatSpeed(speed: Double): String {
        if (speed.isNaN()) {
            return "- -"
        }
        return unitFormatter.format(speed, SpeedUnits.KILOMETERS_PER_HOUR)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigate(RouteMonitorFragmentDirections.endRoute())
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
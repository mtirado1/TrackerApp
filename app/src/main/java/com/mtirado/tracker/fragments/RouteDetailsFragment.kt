package com.mtirado.tracker.fragments

import com.mtirado.tracker.domain.route.Route
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.data.RouteRepository
import com.mtirado.tracker.databinding.FragmentRouteDetailsBinding
import com.mtirado.tracker.domain.formatters.UnitsFormatter
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.SpeedUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter

class RouteDetailsFragment: Fragment() {
    private var _binding: FragmentRouteDetailsBinding? = null

    private val binding get() = _binding!!
    private lateinit var repository: RouteRepository

    private var routeId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val id = it.getString("routeId").toString()
            repository = (activity as MainActivity).repository
            repository
                .getRouteById(id)
                .asLiveData()
                .observe(viewLifecycleOwner) { route ->
                    route?.let {route ->
                        routeId = route.id
                        updateDetails(route)
                    }
                }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonDelete.setOnClickListener {
            routeId?.let {
                repository.deleteRoute(it)
                findNavController().navigate(RouteDetailsFragmentDirections.deleteRoute())
            }
        }
    }

    private fun updateDetails(route: Route) {
        val unit = DistanceUnits.KILOMETERS
        val speedUnit = SpeedUnits.KILOMETERS_PER_HOUR
        binding.title.text = route.name
        binding.distance.text = UnitsFormatter().format(route.path.distance, unit)
        binding.distanceUnit.text = unit.toString()

        binding.duration.text = TimeFormatter().format(route.path.duration)

        binding.averageSpeed.text = UnitsFormatter().format(route.path.speed, speedUnit)
        binding.averageSpeedUnit.text = speedUnit.toString()

        binding.maxSpeed.text = UnitsFormatter().format(route.path.maxSpeed() ?: 0.0, speedUnit)
        binding.maxSpeedUnit.text = speedUnit.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
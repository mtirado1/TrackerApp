package com.mtirado.tracker.fragments

import com.mtirado.tracker.domain.route.Route
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.databinding.FragmentRouteDetailsBinding
import com.mtirado.tracker.domain.formatters.UnitsFormatter
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter

class RouteDetailsFragment: Fragment() {
    private var _binding: FragmentRouteDetailsBinding? = null

    private val binding get() = _binding!!

    private var route: Route? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val id = it.getString("routeId").toString()
            (activity as MainActivity).repository
                .getRouteById(id)
                .asLiveData()
                .observe(viewLifecycleOwner) { route ->
                    route?.let {route -> updateDetails(route)}
                }
        }
        return binding.root
    }

    private fun updateDetails(route: Route) {
        val unit = DistanceUnits.KILOMETERS
        binding.title.text = route.name
        binding.distance.text = UnitsFormatter().format(route.path.distance, unit)
        binding.distanceUnit.text = unit.toString()
        binding.duration.text = TimeFormatter().format(route.path.duration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
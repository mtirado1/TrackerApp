package com.mtirado.tracker.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.adapters.RouteListItemAdapter
import com.mtirado.tracker.databinding.FragmentRouteListBinding
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter
import com.mtirado.tracker.domain.formatters.UnitsFormatter
import com.mtirado.tracker.domain.route.Route
import io.reactivex.disposables.Disposable

class RouteListFragment: Fragment() {
    private var _binding: FragmentRouteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var subscription: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.routeList
        recyclerView.adapter = RouteListItemAdapter(listOf())

        val repository = (activity as MainActivity).repository
        repository.allRoutes.asLiveData().observe(viewLifecycleOwner, Observer { routes ->
            routes?.let {
                binding.noSavedRoutes.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
                (recyclerView.adapter as RouteListItemAdapter).update(routes)
            }
        })

        binding.activeRoute.activeRoute.visibility = View.GONE
        binding.newRouteButton.visibility = View.VISIBLE
        binding.activeRoute.activeRoute.setOnClickListener {
            findNavController().navigate(RouteListFragmentDirections.viewActiveRoute())
        }

        val routeMonitor = (activity as MainActivity).monitor
        routeMonitor.last?.let { updateActiveRoute(it, routeMonitor.isRunning) }
        subscription = routeMonitor.routeObservable.subscribe { updateActiveRoute(it, true) }

        binding.newRouteButton.setOnClickListener {
            val action = RouteListFragmentDirections.actionRouteListToRouteCreation()
            view.findNavController().navigate(action)
        }
    }

    private fun updateActiveRoute(route: Route, running: Boolean) {
        with(binding) {
            if (running) {
                activeRoute.activeRouteIcon.setImageResource(android.R.drawable.ic_menu_mylocation)
            } else {
                activeRoute.activeRouteIcon.setImageResource(android.R.drawable.ic_media_pause)
            }
            activeRoute.activeRoute.visibility = View.VISIBLE
            newRouteButton.visibility = View.GONE
            val unit = DistanceUnits.KILOMETERS
            activeRoute.title.text = route.name
            activeRoute.distance.text = UnitsFormatter().format(route.path.distance, unit)
            activeRoute.unit.text = unit.toString()
            activeRoute.duration.text = TimeFormatter().format(route.path.duration)
            activeRoute.size.text = "(${route.path.size})"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.dispose()
        _binding = null
    }
}
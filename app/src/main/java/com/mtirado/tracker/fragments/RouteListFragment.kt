package com.mtirado.tracker.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.adapters.RouteListItemAdapter
import com.mtirado.tracker.databinding.FragmentRouteListBinding
import com.mtirado.tracker.domain.RouteMonitor
import com.mtirado.tracker.domain.formatters.DistanceUnits
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
    ): View? {
        // Retrieve and inflate the layout for this fragment
        _binding = FragmentRouteListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.routeList
        recyclerView.adapter = RouteListItemAdapter(listOf())

        val repository = (activity as MainActivity).repository
        repository.allRoutes.asLiveData().observe(viewLifecycleOwner, Observer { routes ->
            routes?.let { (recyclerView.adapter as RouteListItemAdapter).update(routes) }
        })

        binding.activeRoute.activeRoute.visibility = View.GONE
        binding.activeRoute.activeRoute.setOnClickListener {
            findNavController().navigate(RouteListFragmentDirections.viewActiveRoute())
        }


        val routeMonitor = (activity as MainActivity).monitor
        routeMonitor.last?.let { updateActiveRoute(it) }
        subscription = routeMonitor.routeObservable.subscribe { updateActiveRoute(it) }

        binding.newRouteButton.setOnClickListener {
            val action = RouteListFragmentDirections.actionRouteListToRouteCreation()
            view.findNavController().navigate(action)
        }
    }

    private fun updateActiveRoute(route: Route) {
        binding.activeRoute.activeRoute.visibility = View.VISIBLE
        val unit = DistanceUnits.KILOMETERS
        binding.activeRoute.title.text = route.name
        binding.activeRoute.distance.text = UnitsFormatter().format(route.path.distance, unit)
        binding.activeRoute.unit.text = unit.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.dispose()
        _binding = null
    }
}
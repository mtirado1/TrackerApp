package com.mtirado.tracker.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mtirado.tracker.MainActivity
import com.mtirado.tracker.adapters.RouteListItemAdapter
import com.mtirado.tracker.databinding.FragmentRouteListBinding
import com.mtirado.tracker.domain.route.Route

class RouteListFragment: Fragment() {
    private var _binding: FragmentRouteListBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

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

        binding.newRouteButton.setOnClickListener {
            val action = RouteListFragmentDirections.actionRouteListToRouteCreation()
            view.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
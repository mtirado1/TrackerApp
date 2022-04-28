package com.mtirado.tracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.mtirado.tracker.databinding.FragmentRouteCreationBinding

class RouteCreationFragment: Fragment() {
    private var _binding: FragmentRouteCreationBinding? = null
    private val binding get() = _binding!!

    private var routeName: String = "New Route"
    private var logInterval: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textRouteName.setText(routeName)
        binding.textLogInterval.setText(logInterval.toString())

        binding.textRouteName.doOnTextChanged { text, _, _, _ ->
            routeName = text.toString()
        }
        binding.textLogInterval.doOnTextChanged { text, _, _, _ ->
            logInterval = text.toString().toIntOrNull() ?: 0
        }

        binding.buttonStart.setOnClickListener {
            val action = RouteCreationFragmentDirections.actionRouteCreationToRouteMonitor(routeName, logInterval)
            view.findNavController().navigate(action)
        }
    }
}
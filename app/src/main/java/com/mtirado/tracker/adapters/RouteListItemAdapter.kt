package com.mtirado.tracker.adapters

import com.mtirado.tracker.domain.route.Route
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mtirado.tracker.R
import com.mtirado.tracker.fragments.RouteListFragmentDirections
import com.mtirado.tracker.domain.formatters.UnitsFormatter
import com.mtirado.tracker.domain.formatters.DistanceUnits
import com.mtirado.tracker.domain.formatters.TimeFormatter

class RouteListItemAdapter(
    var data: List<Route>): RecyclerView.Adapter<RouteListItemAdapter.RouteListItemViewHolder>() {

    fun update(routes: List<Route>) {
        data = routes
        notifyDataSetChanged()
    }

    class RouteListItemViewHolder(public val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val distance: TextView = view.findViewById(R.id.distance)
        val distanceUnit: TextView = view.findViewById(R.id.unit)
        val duration: TextView = view.findViewById(R.id.duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteListItemViewHolder {
        val adapterLayout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.track_list_item, parent, false)
        return RouteListItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RouteListItemViewHolder, position: Int) {
        val route = data[position]
        val unit = DistanceUnits.KILOMETERS
        holder.title.text = route.name
        holder.distance.text = UnitsFormatter().format(route.path.distance, unit)
        holder.distanceUnit.text = unit.toString()
        holder.duration.text = TimeFormatter().format(route.path.duration)
        holder.view.setOnClickListener {
            val action = RouteListFragmentDirections.actionRouteListToRouteDetails(routeId = route.id)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
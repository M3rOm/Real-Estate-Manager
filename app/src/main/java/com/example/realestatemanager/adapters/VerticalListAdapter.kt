package com.example.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.adapters.VerticalListAdapter.ViewHolder

class VerticalListAdapter(private val stringList: List<String>?) :
    Adapter<ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            layout.vertical_list_layout, viewGroup, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        i: Int
    ) {
        val text = stringList!![i]
        viewHolder.pointOfInterestTextView.text = text
    }

    override fun getItemCount(): Int {
        return stringList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pointOfInterestTextView: TextView = itemView.findViewById(id.points_of_interest_text_view)
    }
}
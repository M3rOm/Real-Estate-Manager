package com.example.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.adapters.PointsOfInterestAdapter.ViewHolder
//Recycle view adapter for POIs
class PointsOfInterestAdapter(private val pointsOfInterest: List<String>?) :
    Adapter<ViewHolder>() {

    private var deleteItemListener: DeleteItemListener? = null
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            layout.points_of_interest_item, viewGroup, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        i: Int
    ) {
        viewHolder.textView.text = pointsOfInterest!![i]
    }

    override fun getItemCount(): Int {
        return pointsOfInterest?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(id.points_of_interest_text_view)
        private val deleteIcon: ImageView = itemView.findViewById(id.points_of_interest_item_delete_icon)

        init {
            deleteIcon.setOnClickListener {
                if (deleteItemListener != null) {
                    deleteItemListener!!.onDeleteIconPress(adapterPosition)
                }
            }
        }
    }

    fun setDeleteItemListener(deleteItemListener: DeleteItemListener?) {
        this.deleteItemListener = deleteItemListener
    }

    interface DeleteItemListener {
        fun onDeleteIconPress(position: Int)
    }
}
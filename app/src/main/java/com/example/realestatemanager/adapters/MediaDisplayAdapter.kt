package com.example.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.realestatemanager.R.drawable
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.adapters.MediaDisplayAdapter.ViewHolder
import com.example.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
//RecyclerView adapter for listings' images
class MediaDisplayAdapter(
    private val mediaList: List<String>?,
    private val displayRemoveIcon: Boolean,
    private val context: Context
) : Adapter<ViewHolder>() {

    private var itemDeleteListener: ItemDeleteListener? = null
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            layout.media_display_item, viewGroup, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        i: Int
    ) {
        if (displayRemoveIcon) {
            viewHolder.deleteIcon.visibility = View.VISIBLE
        } else {
            viewHolder.deleteIcon.visibility = View.INVISIBLE
        }
        if (Utils.isInternetAvailable(context)) {
            Picasso.get().load(mediaList!![i]).into(viewHolder.imageView)
        } else {
            viewHolder.imageView.setImageDrawable(context.resources.getDrawable(drawable.internet_access_error))
        }
    }

    override fun getItemCount(): Int {
        return mediaList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(id.media_display_item_image_view)
        val deleteIcon: ImageView = itemView.findViewById(id.media_display_item_delete_icon)

        init {
            deleteIcon.setOnClickListener {
                if (itemDeleteListener != null) {
                    itemDeleteListener!!.deleteIconClicked(adapterPosition)
                }
            }
        }
    }

    fun setOnDeleteIconListener(itemDeleteListener: ItemDeleteListener?) {
        this.itemDeleteListener = itemDeleteListener
    }

    interface ItemDeleteListener {
        fun deleteIconClicked(position: Int)
    }
}
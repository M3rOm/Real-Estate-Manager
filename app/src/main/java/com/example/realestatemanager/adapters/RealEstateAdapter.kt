package com.example.realestatemanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.realestatemanager.R.color
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string
import com.example.realestatemanager.adapters.RealEstateAdapter.ViewHolder
import com.example.realestatemanager.model.RealEstate
import com.example.realestatemanager.utils.Constants.Currencies
import com.example.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import java.text.NumberFormat

//RecyclerView for listings
class RealEstateAdapter(
        private val context: Context,
        private val realEstateList: List<RealEstate>?,
        private var currency: String
) : Adapter<ViewHolder>() {

    private var selectedPosition = 0
    private var listener: OnItemSelectedListener? =
            null

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                layout.listining_item, parent, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
            holder: ViewHolder, @SuppressLint(
                    "RecyclerView"
            ) position: Int
    ) {
        val (_, type, _, _, _, description, _, photos, _, _, status, _, _, _, price1) = realEstateList!![position]
        holder.description.text = description
        var price = -1
        try {
            price = price1.toInt()
        } catch (ignored: Exception) {
        }
        if (price != -1) {
            if (currency == Currencies.DOLLAR) {
                holder.price.text = "$" + NumberFormat.getInstance().format(price.toLong())
            } else if (currency == Currencies.EURO) {
                holder.price.text = "€" + NumberFormat.getInstance().format(Utils.convertDollarToEuro(price).toLong())
            }
        } else {
            holder.price.text = context.getString(string.price_not_set_yet)
        }
        holder.type.text = type
        holder.status.text = status
        Picasso.get().load(photos[0]).into(holder.imageView)
        if (selectedPosition == position) {
            holder.cardView.setBackgroundColor(
                    context.resources.getColor(color.colorCustomLightGrey)
            )
        } else {
            holder.cardView.setBackgroundColor(
                    context.resources.getColor(color.white)
            )
        }
        holder.cardView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            listener!!.onSelection(position)
        }
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun getItemCount(): Int {
        return realEstateList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(id.listining_item_image_view)
        val type: TextView = itemView.findViewById(id.listining_item_type)
        val description: TextView = itemView.findViewById(id.listining_item_description)
        val price: TextView = itemView.findViewById(id.listining_item_price)
        val cardView: CardView = itemView.findViewById(id.listining_item_card_view)
        val status: TextView = itemView.findViewById(id.listining_item_status)
    }

    fun setOnSelectionItem(listener: OnItemSelectedListener?) {
        this.listener = listener
    }

    interface OnItemSelectedListener {
        fun onSelection(position: Int)
    }
}
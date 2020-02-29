package com.example.realestatemanager.utils

import android.content.Context
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.example.realestatemanager.R.dimen
import com.example.realestatemanager.R.drawable
import com.example.realestatemanager.model.ClusterMarker

class ClustersRenderer(
    context: Context, googleMap: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker>?
) : DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {

    private val iconGenerator: IconGenerator = IconGenerator(context.applicationContext)
    private val imageView: ImageView = ImageView(context.applicationContext)
    override fun onBeforeClusterItemRendered(
        item: ClusterMarker,
        markerOptions: MarkerOptions
    ) {
        if (item.iconPicture != null) {
            imageView.setImageBitmap(item.iconPicture)
        } else {
            imageView.setImageResource(drawable.internet_access_error)
        }
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }

    init {
        // initialization cluster item icon generator
        val markerWidth = context.resources.getDimension(dimen.custom_marker_image).toInt()
        val markerHeight = context.resources.getDimension(dimen.custom_marker_image).toInt()
        imageView.layoutParams = LayoutParams(markerWidth, markerHeight)
        val padding = context.resources.getDimension(dimen.custom_marker_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }
}
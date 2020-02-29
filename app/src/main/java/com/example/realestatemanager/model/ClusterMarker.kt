package com.example.realestatemanager.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterMarker(
    // required field
    private var position: LatLng,
    // required field
    private var title: String,
    // required field
    private var snippet: String,
    var iconPicture: Bitmap
) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }
}
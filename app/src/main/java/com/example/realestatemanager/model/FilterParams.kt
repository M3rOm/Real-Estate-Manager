package com.example.realestatemanager.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterParams (
    var minNumOfRooms: String? = Int.MIN_VALUE.toString(),
    var maxNumOfRooms: String? = Int.MAX_VALUE.toString(),
    var minNumOfBedRooms: String? = Int.MIN_VALUE.toString(),
    var maxNumOfBedRooms: String? = Int.MAX_VALUE.toString(),
    var minSurfaceArea: String? = Int.MIN_VALUE.toString(),
    var maxSurfaceArea: String? = Int.MAX_VALUE.toString(),
    var isSold: Boolean= false,
    var isAvailable : Boolean = false
) : Parcelable


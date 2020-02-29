package com.example.realestatemanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.realestatemanager.utils.ListTypeConverters
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "realEstateListings")
@TypeConverters(ListTypeConverters::class)
data class RealEstate(

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var type: String = "",
    var numbOfBedRooms: Int= 0,
    var surfaceArea : Int= 0,
    var numberOfRooms : Int= 0,
    var description: String = "",
    var longDescription: String = "",
    var photos:  MutableList<String> = ArrayList(),
    var address: String = "",
    var pointsOfInterest: MutableList<String> = ArrayList(),
    var status: String = "",
    var datePutInMarket: Long = 0,
    var saleData: Long = 0,
    var agent: String = "",
    var price: String = ""
): Parcelable

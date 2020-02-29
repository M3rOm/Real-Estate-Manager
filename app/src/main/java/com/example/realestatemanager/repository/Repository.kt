package com.example.realestatemanager.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.realestatemanager.db.RealEstateDao
import com.example.realestatemanager.db.RoomDB.Companion.getInstance
import com.example.realestatemanager.db.transactions.DeleteTask
import com.example.realestatemanager.db.transactions.InsertTask
import com.example.realestatemanager.db.transactions.UpdateTask
import com.example.realestatemanager.model.FilterParams
import com.example.realestatemanager.model.RealEstate
import com.example.realestatemanager.utils.Constants.Status

//Class to mediate between different data sources like SQLite and webservers.
//So ViewModel will have a consolidated data flow.
//Repository class calls DAOs directly
class Repository(context: Context) {
    private val dao: RealEstateDao? = getInstance(context)?.dao
    fun insertListing(realEstate: RealEstate?) {
        dao?.let { InsertTask(it).execute(realEstate) }
    }

    fun updateListing(realEstate: RealEstate?) {
        dao?.let { UpdateTask(it).execute(realEstate) }
    }

    fun deleteListing(realEstate: RealEstate?) {
        dao?.let { DeleteTask(it).execute(realEstate) }
    }

    val allListings: LiveData<List<RealEstate>>?
        get() = dao?.allListings

    fun filterList(filterParams: FilterParams): LiveData<List<RealEstate>>? {
        var soldTerm = ""
        var availableTerm = ""
        if (filterParams.isSold) {
            soldTerm = Status.SOLD
        }
        if (filterParams.isAvailable) {
            availableTerm = Status.AVAILABLE
        }
        return dao!!.getFilteredListing(
            filterParams.minSurfaceArea,
            filterParams.maxSurfaceArea
            , filterParams.minNumOfRooms
            , filterParams.maxNumOfRooms
            , filterParams.minNumOfBedRooms
            , filterParams.maxNumOfBedRooms
            , soldTerm
            , availableTerm
        )
    }

    fun geSearchedListings(term: String?): LiveData<List<RealEstate>>? {
        return dao?.getSearchedListing(term)
    }
}
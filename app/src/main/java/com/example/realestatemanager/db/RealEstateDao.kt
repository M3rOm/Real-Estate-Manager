package com.example.realestatemanager.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.realestatemanager.model.RealEstate
//DAOs have methods for abstract access to db files (Insert, Update, Delete)
//They return the results in a List wrapped by LiveData
@Dao
interface RealEstateDao {

    //return can be void but the long[] is the rows data were inserted
    @Insert
    fun insertRealEstate(vararg realEstates: RealEstate?): LongArray?

    //return can be void but the int is how many rows were updated
    @Update
    fun updateRealEstate(vararg realEstates: RealEstate?): Int

    //return can be void but the int is how many rows were deleted
    @Delete
    fun deleteRealEstate(vararg realEstates: RealEstate?): Int

    @get:Query("SELECT * FROM realEstateListings") //all the properties in the db
    val allListings: LiveData<List<RealEstate>>?

    @Query(
        "SELECT * FROM realEstateListings WHERE surfaceArea BETWEEN :minSurface  AND :maxSurface"
                + " AND numberOfRooms BETWEEN  :minNumOfRooms AND :maxNumOfRooms "
                + "AND numbOfBedRooms BETWEEN :minNumOfBedrooms AND :maxNumOfBedrooms "
                + "AND (status LIKE :sold OR status LIKE :available)"
    )
    fun getFilteredListing(
        minSurface: String?, maxSurface: String?
        , minNumOfRooms: String?, maxNumOfRooms: String?
        , minNumOfBedrooms: String?, maxNumOfBedrooms: String?
        , sold: String?, available: String?
    ): LiveData<List<RealEstate>>?

    @Query(
        "SELECT * FROM realEstateListings WHERE (address LIKE '%' || :term || '%' " +
                "OR description LIKE '%' || :term || '%' " +
                "OR type LIKE '%' || :term || '%')"
    )
    fun getSearchedListing(
        term: String?
    ): LiveData<List<RealEstate>>?

    @get:Query("SELECT * FROM realEstateListings")
    val realEstateWithCursor: Cursor?
}
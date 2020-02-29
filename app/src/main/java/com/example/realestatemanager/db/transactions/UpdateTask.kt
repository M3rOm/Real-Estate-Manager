package com.example.realestatemanager.db.transactions

import android.os.AsyncTask
import com.example.realestatemanager.db.RealEstateDao
import com.example.realestatemanager.model.RealEstate

class UpdateTask(private val dao: RealEstateDao) : AsyncTask<RealEstate?, Void?, Void?>() {
    override fun doInBackground(vararg params: RealEstate?): Void? {
        dao.updateRealEstate(*params)
        return null
    }

}
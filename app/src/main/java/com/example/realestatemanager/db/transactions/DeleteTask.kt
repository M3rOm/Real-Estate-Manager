package com.example.realestatemanager.db.transactions

import android.os.AsyncTask
import com.example.realestatemanager.db.RealEstateDao
import com.example.realestatemanager.model.RealEstate
//Transactions are asynchronous tasks, to edit database via DAO.
class DeleteTask(private val dao: RealEstateDao) : AsyncTask<RealEstate?, Void?, Void?>() {

    override fun doInBackground(vararg params: RealEstate?): Void? {
        dao.deleteRealEstate(*params)
        return null
    }

    override fun onProgressUpdate(vararg values: Void?) {
        // give progress updates about the background job
        //eg progress bar 10% 20% 100%
    }

    override fun onPostExecute(result: Void?) {
        //what should this task do once the job is done
        //eg. Upload Success / Upload Failed=
    }
}
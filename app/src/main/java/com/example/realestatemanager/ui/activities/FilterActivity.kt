package com.example.realestatemanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.model.FilterParams
import com.example.realestatemanager.utils.Constants.BundleKeys
import com.example.realestatemanager.utils.Constants.TypesList

class FilterActivity : AppCompatActivity() {
    private var filterBtn: Button? = null
    private var filterParams: FilterParams? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_filter)
        filterParams = FilterParams()
        setToolbar()
        setViews()
        setListeners()
    }

    private fun setToolbar() {
        val toolbar =
            findViewById<Toolbar>(id.filter_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setViews() {
        filterBtn = findViewById(id.filter_btn)
    }

    private fun setListeners() {
        filterBtn!!.setOnClickListener {
            updateParams()
            val intent = Intent(
                this@FilterActivity
                , NavigationActivity::class.java
            )
            val bundle = Bundle()
            bundle.putInt(TypesList.TYPE_LIST_KEY, TypesList.FILTERED)
            bundle.putParcelable(BundleKeys.FILTERED_PARAMS_KEY, filterParams)
            intent.putExtra(BundleKeys.BUNDLE_EXTRA, bundle)
            startActivity(intent)
        }
    }

    private fun updateParams() {
        val available = findViewById<CheckBox>(id.activity_filter_available)
        val sold = findViewById<CheckBox>(id.activity_filter_sold)
        val startNumOfRooms = findViewById<EditText>(id.activity_filter_start_number_of_rooms)
        val endNumOfRooms = findViewById<EditText>(id.activity_filter_end_number_of_rooms)
        val startNumOfBedRooms = findViewById<EditText>(id.activity_filter_start_number_of_bedrooms)
        val endNumOfBedRooms = findViewById<EditText>(id.activity_filter_end_number_of_bedrooms)
        val startSurfaceArea = findViewById<EditText>(id.activity_filter_start_surface)
        val endSurfaceArea = findViewById<EditText>(id.activity_filter_end_surface)
        filterParams!!.isAvailable = available.isChecked
        filterParams!!.isSold = sold.isChecked
        if (startNumOfRooms.text.toString().isNotEmpty()) {
            filterParams!!.minNumOfRooms = startNumOfRooms.text.toString()
        }
        if (endNumOfRooms.text.toString().isNotEmpty()) {
            filterParams!!.maxNumOfRooms = endNumOfRooms.text.toString()
        }
        if (startNumOfBedRooms.text.toString().isNotEmpty()) {
            filterParams!!.minNumOfBedRooms = startNumOfBedRooms.text.toString()
        }
        if (endNumOfBedRooms.text.toString().isNotEmpty()) {
            filterParams!!.maxNumOfBedRooms = endNumOfBedRooms.text.toString()
        }
        if (startSurfaceArea.text.toString().isNotEmpty()) {
            filterParams!!.minSurfaceArea = startSurfaceArea.text.toString()
        }
        if (endSurfaceArea.text.toString().isNotEmpty()) {
            filterParams!!.maxSurfaceArea = endSurfaceArea.text.toString()
        }
    }
}
package com.example.realestatemanager.ui.activities

import android.app.Activity
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string
import com.example.realestatemanager.adapters.MediaDisplayAdapter
import com.example.realestatemanager.adapters.MediaDisplayAdapter.ItemDeleteListener
import com.example.realestatemanager.adapters.PointsOfInterestAdapter
import com.example.realestatemanager.adapters.PointsOfInterestAdapter.DeleteItemListener
import com.example.realestatemanager.model.RealEstate
import com.example.realestatemanager.repository.Repository
import com.example.realestatemanager.ui.fragments.DatePickerFragment
import com.example.realestatemanager.utils.Constants.BundleKeys
import com.example.realestatemanager.utils.Constants.Currencies
import com.example.realestatemanager.utils.Constants.Status
import com.example.realestatemanager.utils.Utils
import com.example.realestatemanager.utils.Utils.convertEuroToDollar
import com.example.realestatemanager.utils.Utils.createNotification
import com.example.realestatemanager.utils.Utils.formatDate
import com.example.realestatemanager.utils.Utils.getCurrency
import com.example.realestatemanager.utils.Utils.todayDate
import java.util.ArrayList
import java.util.Calendar
import java.util.UUID

class FormActivity : AppCompatActivity(), OnClickListener, OnDateSetListener {
    private var realEstate: RealEstate? = null
    private var shortDescription: EditText? = null
    private var longDescription: EditText? = null
    private var media: EditText? = null
    private var type: EditText? = null
    private var numOfRooms: EditText? = null
    private var location: EditText? = null
    private var surface: EditText? = null
    private var price: EditText? = null
    private var numOfBedrooms: EditText? = null
    private var agentResposible: EditText? = null
    private var mediaRecyclerView: RecyclerView? = null
    private var mediaDisplayAdapter: MediaDisplayAdapter? = null
    private var pointsOfInterestAdapter: PointsOfInterestAdapter? = null
    private var pointsOfInterestRecyclerView: RecyclerView? = null
    private var pointsOfInterestEditText: EditText? = null
    private var repository: Repository? = null
    private var dateContainer: View? = null
    private var soldDateTextView: TextView? = null
    private var soldDate: Long = 0
    private var updating = false
    private var soldRadio: RadioButton? = null
    private var availableRadio: RadioButton? = null
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    private var priceTextView: TextView? = null
    private var currency: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_form)
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        setToolbar()
        realEstate =
            intent.getParcelableExtra(BundleKeys.REAL_ESTATE_OBJECT_KEY)
        repository = Repository(this@FormActivity)
        setViews()
        setCurrency()
        setParams()
        setClickListners()
    }
    //Setting views
    private fun setViews() {
        priceTextView = findViewById(id.activity_update_and_add_price_text_view)
        val pointsOfIntAdd =
            findViewById<ImageView>(id.activity_update_and_add_points_of_interest_add_icon)
        shortDescription = findViewById(id.activity_update_and_add_short_description_edit_text)
        longDescription = findViewById(id.activity_update_and_add_long_description_edit_text)
        val mediaAdd =
            findViewById<ImageView>(id.activity_update_and_add_media_add_icon)
        mediaRecyclerView = findViewById(id.activity_update_and_add_media_recycler_view)
        media = findViewById(id.activity_update_and_add_media_edit_text)
        type = findViewById(id.activity_update_and_add_type_edit_text)
        numOfRooms = findViewById(id.activity_update_and_add_num_of_rooms_edit_text)
        surface = findViewById(id.activity_update_and_add_surface_edit_text)
        location = findViewById(id.activity_update_and_add_location)
        val submitBtn =
            findViewById<Button>(id.activity_update_and_add_submit_btn)
        val deleteBtn = findViewById<Button>(id.activity_update_and_add_delete_btn)
        pointsOfInterestRecyclerView =
            findViewById(id.activity_update_and_add_points_of_interest_recycler_view)
        pointsOfInterestEditText = findViewById(id.activity_update_and_add_v_edit_text)
        price = findViewById(id.activity_update_and_add_price)
        numOfBedrooms = findViewById(id.activity_update_and_add_num_of_bedrooms)
        soldRadio = findViewById(id.activity_update_and_add_sold_radio)
        availableRadio = findViewById(id.activity_update_and_add_available_radio)
        soldDateTextView = findViewById(id.activity_update_and_add_sold_date)
        dateContainer = findViewById(id.activity_update_and_add_sold_date_container)
        val selectPicFromInternalStorageBtn =
            findViewById<Button>(id.activity_update_and_add_picture_from_storage)
        agentResposible = findViewById(id.activity_update_and_add_agent_responsible)
        mediaAdd.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
        pointsOfIntAdd.setOnClickListener(this)
        selectPicFromInternalStorageBtn.setOnClickListener(this)
    }

    private fun setCurrency() {
        currency = intent.getStringExtra(BundleKeys.BUNDLE_CURRENCY_KEY)
        if (currency == null) {
            currency = getCurrency(this@FormActivity)
        }
        if (currency == Currencies.EURO) {
            priceTextView!!.text = getString(string.price_in_euros)
        }
    }

    private fun setClickListners() {
        soldRadio!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dateContainer!!.visibility = View.VISIBLE
                soldDateTextView?.text = todayDate
            } else {
                dateContainer!!.visibility = View.GONE
            }
        }
        soldDateTextView!!.setOnClickListener {
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")
        }
    }

    private fun setToolbar() {
        val toolbar =
            findViewById<Toolbar>(id.update_and_add_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setParams() {
        if (realEstate != null) {
            updating = true
            shortDescription!!.setText(realEstate!!.description)
            longDescription!!.setText(realEstate!!.longDescription)
            val numberOfRooms = realEstate!!.numberOfRooms
            if (numberOfRooms > 0) {
                numOfRooms!!.setText(numberOfRooms.toString())
            }
            type!!.setText(realEstate!!.type)
            val surfaceArea = realEstate!!.surfaceArea
            if (surfaceArea > 0) {
                surface!!.setText(surfaceArea.toString())
            }
            try {
                var priceString: String? = realEstate!!.price
                val priceInt = Integer.valueOf(priceString!!)
                if (priceInt > 0) {
                    if (currency == Currencies.EURO) {
                        priceString = java.lang.String.valueOf(Utils.convertDollarToEuro(priceInt))
                    }
                    price!!.setText(priceString)
                }
            } catch (ignored: Exception) {
            }
            agentResposible!!.setText(realEstate!!.agent)
            location!!.setText(realEstate!!.address)
            val numbOfBedRooms = realEstate!!.numbOfBedRooms
            if (numbOfBedRooms > 0) {
                numOfBedrooms!!.setText(numbOfBedRooms.toString())
            }
            if (realEstate!!.status == Status.SOLD) {
                soldRadio!!.isChecked = true
                availableRadio!!.isChecked = false
                dateContainer!!.visibility = View.VISIBLE
                soldDateTextView?.text = formatDate(realEstate!!.saleData)
            }
        } else {
            updating = false
            realEstate = RealEstate()
            realEstate!!.photos = ArrayList()
            realEstate!!.pointsOfInterest = ArrayList()
            realEstate!!.pointsOfInterest = ArrayList()
        }
        setMediaRecyclerView()
        setPointsOfInterestRecyclerView()
    }

    private fun setMediaRecyclerView() {
        mediaDisplayAdapter = MediaDisplayAdapter(
            realEstate!!.photos, true
            , applicationContext
        )
        val layoutManager = LinearLayoutManager(
            this
            , LinearLayoutManager.HORIZONTAL, false
        )
        mediaRecyclerView?.layoutManager = layoutManager
        mediaDisplayAdapter?.setOnDeleteIconListener(object : ItemDeleteListener {
            override fun deleteIconClicked(position: Int) {
                realEstate?.photos?.removeAt(position)
                mediaDisplayAdapter?.notifyDataSetChanged()
            }
        })
        mediaRecyclerView?.adapter = mediaDisplayAdapter
    }

    private fun setPointsOfInterestRecyclerView() {
        pointsOfInterestAdapter = PointsOfInterestAdapter(realEstate!!.pointsOfInterest)
        val layoutManager = LinearLayoutManager(
            this@FormActivity
        )
        pointsOfInterestRecyclerView?.layoutManager = layoutManager
        pointsOfInterestAdapter?.setDeleteItemListener(object : DeleteItemListener {
            override fun onDeleteIconPress(position: Int) {
                realEstate?.pointsOfInterest?.removeAt(position)
                pointsOfInterestAdapter?.notifyDataSetChanged()
            }
        })
        pointsOfInterestRecyclerView?.adapter = pointsOfInterestAdapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            id.activity_update_and_add_media_add_icon -> addMedia(media!!.text.toString())
            id.activity_update_and_add_submit_btn -> submitRealEstate()
            id.activity_update_and_add_delete_btn -> deleteRealEstate()
            id.activity_update_and_add_points_of_interest_add_icon -> addPointsOfInterest()
            id.activity_update_and_add_picture_from_storage -> chooseImage()
        }
    }

    private fun deleteRealEstate() {
        try {
        repository?.deleteListing(realEstate)
        goToNavigationActivity()
    } catch (e: Exception) {
        Toast.makeText(
                this@FormActivity, getString(string.error)
                , Toast.LENGTH_SHORT
        ).show()
    }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            filePath = data.data
            uploadImage()
        }
    }

    private fun addMedia(url: String) {
        if (url.isEmpty()) {
            Toast.makeText(this, "You must add an url", Toast.LENGTH_SHORT).show()
        } else {
            realEstate?.photos?.add(url)
            mediaDisplayAdapter?.notifyDataSetChanged()
            mediaRecyclerView?.layoutManager?.scrollToPosition(realEstate!!.photos.size - 1)
            media?.setText("")
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference!!.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener { ref.downloadUrl.addOnSuccessListener { uri -> addMedia(uri.toString()) } }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        applicationContext, "Failed " + e.message
                        , Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun addPointsOfInterest() {
        val pointOfInterest = pointsOfInterestEditText!!.text.toString()
        if (pointOfInterest.isEmpty()) {
            Toast.makeText(
                this, getString(string.you_must_add_a_point_of_int)
                , Toast.LENGTH_SHORT
            ).show()
        } else {
            realEstate?.pointsOfInterest?.add(pointOfInterest)
            pointsOfInterestAdapter?.notifyDataSetChanged()
            pointsOfInterestEditText?.setText("")
        }
    }

    private fun submitRealEstate() {
        val photos: List<String> = realEstate!!.photos
        if (photos.isEmpty()) {
            Toast.makeText(
                baseContext, getString(string.you_must_add_at_least)
                , Toast.LENGTH_SHORT
            ).show()
        } else {
            try {
                realEstate!!.datePutInMarket = System.currentTimeMillis()
                if (soldRadio!!.isChecked) {
                    realEstate!!.saleData = soldDate
                    realEstate!!.status = Status.SOLD
                } else {
                    realEstate!!.status =
                        Status.AVAILABLE
                }
                var priceString: String? = price!!.text.toString()
                if (currency == Currencies.EURO) {
                    try {
                        priceString =
                            java.lang.String.valueOf(convertEuroToDollar(Integer.valueOf(priceString!!)))
                    } catch (e: Exception) {
                        priceString = ""
                    }
                }
                realEstate!!.price = priceString!!
                val numOfBedInt: Int
                numOfBedInt = try {
                    Integer.valueOf(numOfBedrooms!!.text.toString())
                } catch (ignored: Exception) {
                    -1
                }
                realEstate?.numbOfBedRooms = numOfBedInt
                realEstate?.type = type!!.text.toString()
                realEstate?.description = shortDescription!!.text.toString()
                realEstate?.longDescription = longDescription!!.text.toString()
                val surfaceInt = try {
                    Integer.valueOf(surface?.text.toString())
                } catch (e: Exception) {
                    -1
                }
                val numOfRoomsInt: Int = try {
                    Integer.valueOf(numOfRooms?.text.toString())
                } catch (e: Exception) {
                    -1
                }
                val numOfBedroomsInt: Int
                numOfBedroomsInt = try {
                    numOfBedInt
                } catch (e: Exception) {
                    -1
                }
                realEstate?.numbOfBedRooms = numOfBedroomsInt
                realEstate?.surfaceArea = surfaceInt
                realEstate?.numberOfRooms = numOfRoomsInt
                realEstate?.agent = agentResposible!!.text.toString()
                realEstate?.address = location!!.text.toString()
                if (updating) {
                    val title = realEstate!!.description + " " + getString(string.updated)
                    val message = getString(string.real_estate_updated)
                    createNotification(
                        applicationContext,
                        title,
                        message
                    )
                    repository?.updateListing(realEstate)
                } else {
                    val title = realEstate!!.description + " " + getString(string.added)
                    val message = getString(string.real_estate_added)
                    createNotification(
                        applicationContext,
                        title,
                        message
                    )
                    repository?.insertListing(realEstate)
                }
                goToNavigationActivity()
            } catch (e: Exception) {
                Toast.makeText(
                    this@FormActivity, getString(string.error)
                    , Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun goToNavigationActivity() {
        val intent = Intent(
            this@FormActivity
            , NavigationActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val currentDateString: String = formatDate(calendar)
        soldDate = calendar.timeInMillis
        soldDateTextView!!.text = currentDateString
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
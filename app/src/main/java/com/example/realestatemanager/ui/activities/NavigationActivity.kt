package com.example.realestatemanager.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.realestatemanager.R
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string
import com.example.realestatemanager.adapters.MediaDisplayAdapter
import com.example.realestatemanager.adapters.MediaDisplayAdapter.ItemDeleteListener
import com.example.realestatemanager.adapters.RealEstateAdapter
import com.example.realestatemanager.adapters.RealEstateAdapter.OnItemSelectedListener
import com.example.realestatemanager.adapters.VerticalListAdapter
import com.example.realestatemanager.model.FilterParams
import com.example.realestatemanager.model.RealEstate
import com.example.realestatemanager.repository.Repository
import com.example.realestatemanager.utils.Constants.BundleKeys
import com.example.realestatemanager.utils.Constants.Currencies
import com.example.realestatemanager.utils.Constants.TypesList
import com.example.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import java.util.ArrayList

//Entry point of the app. Only show, if the user is not logged on.
class NavigationActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var listings: MutableList<RealEstate>? = null
    private var repository: Repository? = null
    private var recyclerViewAdapter: RealEstateAdapter? = null
    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var itemDescription: TextView? = null
    private var noEntries: TextView? = null
    private var surface: TextView? = null
    private var numOfRooms: TextView? = null
    private var numOfBedrooms: TextView? = null
    private var type: TextView? = null
    private var agent: TextView? = null
    private var shortDescription: TextView? = null
    private var status: TextView? = null
    private var addedDate: TextView? = null
    private var soldDate: TextView? = null
    private var mediaRecyclerView: RecyclerView? = null
    private var location: TextView? = null
    private var realEstateIndex = 0
    private var listType = 0
    private var map: ImageView? = null
    private var extras: Bundle? = null
    private var mediaDisplayAdapter: MediaDisplayAdapter? = null
    private var currency: String? = null
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.currentUser
        if (currentUser == null) {
            goToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set layout
        setContentView(layout.activity_navigation)
        setConfigs()
        setViews()
        setListingRecyclerView()
        addDataObservers()
        setToolbar()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        configureDrawer()
    }

    //set our user, repository, currency etc. variables and get bundle
    private fun setConfigs() {
        auth = FirebaseAuth.getInstance()
        repository = Repository(this@NavigationActivity)
        realEstateIndex = 0
        currency = intent.getStringExtra(BundleKeys.BUNDLE_CURRENCY_KEY)
        if (currency == null) {
            currency = Utils.getCurrency(this@NavigationActivity)
        }
        extras = intent.getBundleExtra(BundleKeys.BUNDLE_EXTRA)
        listType = if (extras != null) {
            extras!!.getInt(
                    TypesList.TYPE_LIST_KEY,
                    TypesList.ALL
            )
        } else {
            TypesList.ALL
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        toolbar?.title = getString(string.app_name)
    }

    //If there's an address for a listing, show image of it's location on a map.
    private fun setMap(realEstate: RealEstate) {
        val address = realEstate.address
        if (address.isEmpty()) {
            map!!.visibility = View.INVISIBLE
        } else {
            map!!.visibility = View.VISIBLE
            val sb = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                    address +
                    "&markers=%7Ccolor:0xFFFF00%7Clabel:%7C" +
                    address +
                    "&zoom=13&size=600x300&maptype=roadmap&key=" +
                    getString(string.google_maps_api_key)
            Picasso.get().load(sb).into(map)
        }
    }

    //Set up navigation drawer
    private fun configureDrawer() {
        configureDrawerLayout()
        configureNavigationView()
    }

    //Set layout for navigation drawer, set toggle behavior, listener
    private fun configureDrawerLayout() {
        drawerLayout = findViewById(id.activity_navigation_drawer_layout)
        toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar
                , string.navigation_drawer_open, string.navigation_drawer_close
        )
        drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()
        drawerLayout?.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(view: View, v: Float) {}
            override fun onDrawerOpened(view: View) { // blurBackground();
                val userEmailTextView = findViewById<TextView>(id.drawer_header_user_email)
                try {//show user details
                    userEmailTextView.text =
                            FirebaseAuth.getInstance()
                                    .currentUser
                                    ?.email
                } catch (e: Exception) {
                    Log.e(TAG, "configureDrawerLayout: " + e.message)
                }
            }

            override fun onDrawerClosed(view: View) {}
            override fun onDrawerStateChanged(i: Int) {}
        })
    }

    //Sign user out from FirebaseAuth, and divert to LogInActivity
    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        Log.d(TAG, "onClick: user signOut")
        val intent = Intent(
                this@NavigationActivity
                , LogInActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //Set views in the navigation drawer and set up behavior for click events
    private fun configureNavigationView() {
        val navigationView = findViewById<NavigationView>(id.activity_navigation_nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            var intent: Intent? = null
            when (id) {
                //Reopens activity showing all listings
                R.id.menu_drawer_all -> {
                    intent = Intent(
                            this@NavigationActivity
                            , NavigationActivity::class.java
                    )
                    intent.putExtra(
                            TypesList.TYPE_LIST_KEY,
                            TypesList.ALL
                    )
                }
                //Opens map activity
                R.id.menu_drawer_map -> intent = Intent(
                        this@NavigationActivity
                        , MapActivity::class.java
                )
                //Opens filter activity
                R.id.menu_drawer_filter -> intent = Intent(
                        this@NavigationActivity
                        , FilterActivity::class.java
                )
                //Opens Loan simulator activity
                R.id.menu_drawer_sing_out -> signOutUser()
                R.id.menu_drawer_loan -> intent = Intent(
                        this@NavigationActivity
                        , LoanSimulationActivity::class.java
                )
            }
            drawerLayout!!.closeDrawer(GravityCompat.START)
            intent?.let { startActivity(it) }
            true
        }
    }

    //Handle back press differently for drawer open
    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //Set the Views
    private fun setViews() {
        toolbar = findViewById(id.navigation_activity_toolbar)
        itemDescription = findViewById(id.navigation_activity_description)
        map = findViewById(id.map)
        noEntries = findViewById(id.no_entries)
        surface = findViewById(id.navigation_activity_surface)
        numOfRooms = findViewById(id.navigation_activity_num_of_rooms)
        numOfBedrooms = findViewById(id.navigation_activity_num_of_bedrooms)
        location = findViewById(id.navigation_activity_location)
        mediaRecyclerView = findViewById(id.activity_navigation_media_recycler_view)
        type = findViewById(id.navigation_activity_type)
        shortDescription = findViewById(id.navigation_activity_short_description)
        status = findViewById(id.navigation_activity_status)
        addedDate = findViewById(id.navigation_activity_added_date)
        soldDate = findViewById(id.navigation_activity_sold_date)
        agent = findViewById(id.navigation_activity_agent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu.findItem(id.menu_toolbar_search)
        val currencyItem: MenuItem = menu.findItem(id.menu_toolbar_change_currency)
        val searchView =
                item.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        val menuItemEdit = menu.findItem(id.menu_toolbar_edit)
        val menuItemAdd = menu.findItem(id.menu_toolbar_add)
        // final MenuItem menuItemDelete = menu.findItem(R.id.menu_toolbar_delete);
        if (currency == Currencies.EURO) {
            currencyItem.title = getString(string.change_to_d)
        } else if (currency == Currencies.DOLLAR) {
            currencyItem.title = getString(string.change_to_euro)
        }
        currencyItem.setOnMenuItemClickListener {
            if (currency == Currencies.EURO) {
                currency = Currencies.DOLLAR
                Utils.storeCurrency(
                        this@NavigationActivity,
                        Currencies.DOLLAR
                )
                currencyItem.title = getString(string.change_to_euro)
            } else if (currency == Currencies.DOLLAR) {
                currency = Currencies.EURO
                Utils.storeCurrency(
                        this@NavigationActivity,
                        Currencies.EURO
                )
                currencyItem.title = getString(string.change_to_d)
            }
            recyclerViewAdapter!!.setCurrency(currency!!)
            recyclerViewAdapter!!.notifyDataSetChanged()
            false
        }
        searchView.setOnSearchClickListener {
            //search is expanded
            menuItemEdit.isVisible = false
            menuItemAdd.isVisible = false
            //menuItemDelete.setVisible(false);
            currencyItem.isVisible = false
            toggle!!.isDrawerIndicatorEnabled = false
        }
        searchView.setOnCloseListener {
            menuItemEdit.isVisible = true
            menuItemAdd.isVisible = true
            // menuItemDelete.setVisible(true);
            currencyItem.isVisible = true
            toggle!!.isDrawerIndicatorEnabled = true
            false
        }
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                searchTerm(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        //It must return true for the menu to be displayed; if you return false it will not be show
        return true
    }

    private fun searchTerm(term: String) {
        val intent = Intent(
                this@NavigationActivity
                , NavigationActivity::class.java
        )
        val bundle = Bundle()
        bundle.putInt(TypesList.TYPE_LIST_KEY, TypesList.SEARCH)
        bundle.putString(BundleKeys.SEARCH_PARAM_KEY, term)
        intent.putExtra(BundleKeys.BUNDLE_EXTRA, bundle)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.menu_toolbar_add -> goToUpdateAndAddActivity(null)
            id.menu_toolbar_edit -> try {
                goToUpdateAndAddActivity(listings!![realEstateIndex])
            } catch (e: Exception) {
                Toast.makeText(
                        this, "It is not possible to edit"
                        , Toast.LENGTH_SHORT
                ).show()
            }
        }
        return false
    }

    private fun goToUpdateAndAddActivity(realEstate: RealEstate?) {
        val intent = Intent(
                this@NavigationActivity
                , FormActivity::class.java
        )
        intent.putExtra(BundleKeys.REAL_ESTATE_OBJECT_KEY, realEstate)
        intent.putExtra(BundleKeys.BUNDLE_CURRENCY_KEY, currency)
        startActivity(intent)
    }

    private fun addDataObservers() {
        var listLiveData: LiveData<List<RealEstate>>? = null
        when (listType) {
            TypesList.ALL -> listLiveData =
                    repository!!.allListings
            TypesList.FILTERED -> listLiveData =
                    filteredList
            TypesList.SEARCH -> listLiveData = searchList
        }
        listLiveData?.observe(this@NavigationActivity,
                Observer { realEstates ->
                    if (listings!!.size > 0) {
                        listings!!.clear()
                    }
                    if (realEstates != null) {
                        listings!!.addAll(realEstates)
                        recyclerViewAdapter!!.notifyDataSetChanged()
                        displayRealEstateInformation()
                    }
                })
    }

    private val searchList: LiveData<List<RealEstate>>?
        get() {
            val listLiveData: LiveData<List<RealEstate>>?
            val term = extras!!.getString(BundleKeys.SEARCH_PARAM_KEY, "")
            listLiveData = repository!!.geSearchedListings(term)
            return listLiveData
        }

    private val filteredList: LiveData<List<RealEstate>>?
        get() {
            val listLiveData: LiveData<List<RealEstate>>?
            val filterParams: FilterParams? = extras!!.getParcelable(BundleKeys.FILTERED_PARAMS_KEY)
            listLiveData = filterParams?.let { repository!!.filterList(it) }
            return listLiveData
        }

    //Horizontal RecyclerView for showing listings
    private fun setListingRecyclerView() {
        listings = ArrayList()
        val layoutManager = LinearLayoutManager(
                this
                , LinearLayoutManager.HORIZONTAL, false
        )
        val recyclerView: RecyclerView = findViewById(id.activity_navigation_recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RealEstateAdapter(
                this@NavigationActivity,
                listings, currency!!
        )
        recyclerViewAdapter!!.setOnSelectionItem(object :
                OnItemSelectedListener {
            override fun onSelection(position: Int) {
                realEstateIndex = position
                displayRealEstateInformation()
            }
        })
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setPointsOfInterestRecyclerView() {
        @SuppressLint("WrongConstant") val layoutManager = LinearLayoutManager(
                this
                , LinearLayoutManager.VERTICAL, false
        )
        val recyclerView: RecyclerView = findViewById(id.points_of_interest_recycler_view)
        recyclerView.layoutManager = layoutManager
        val verticalListAdapter = VerticalListAdapter(
                listings!![realEstateIndex].pointsOfInterest
        )
        recyclerView.adapter = verticalListAdapter
    }
    //Show info for a specific listing
    private fun displayRealEstateInformation() = if (listings!!.size > 0) {
        val realEstate = listings!![realEstateIndex]
        itemDescription!!.text = realEstate.longDescription
        setPointsOfInterestRecyclerView()
        val surfaceArea = realEstate.surfaceArea
        if (surfaceArea > 0) {
            surface!!.text = surfaceArea.toString()
        } else {
            surface!!.text = ""
        }
        val numberOfRooms = realEstate.numberOfRooms
        if (numberOfRooms > 0) {
            numOfRooms!!.text = numberOfRooms.toString()
        } else {
            numOfRooms!!.text = ""
        }
        val numbOfBedRooms = realEstate.numbOfBedRooms
        if (numbOfBedRooms > 0) {
            numOfBedrooms!!.text = numbOfBedRooms.toString()
        } else {
            numOfBedrooms!!.text = ""
        }
        location!!.text = realEstate.address
        agent!!.text = realEstate.agent
        type!!.text = realEstate.type
        setMediaRecyclerView(realEstate)
        setMap(realEstate)
        shortDescription!!.text = realEstate.description
        status!!.text = realEstate.status
        addedDate!!.text = Utils.formatDate(realEstate.datePutInMarket)
        val soldDateLong = realEstate.saleData
        if (soldDateLong > 0) {
            soldDate!!.text = Utils.formatDate(soldDateLong)
        } else {
            soldDate!!.text = getString(string.not_sold_yet)
        }
        noEntries!!.visibility = View.GONE
    } else {
        noEntries!!.visibility = View.VISIBLE
    }

    private fun setMediaRecyclerView(realEstate: RealEstate) {
        mediaDisplayAdapter = MediaDisplayAdapter(
                realEstate.photos, false
                , applicationContext
        )
        val layoutManager = LinearLayoutManager(
                this
                , LinearLayoutManager.HORIZONTAL, false
        )
        mediaRecyclerView!!.layoutManager = layoutManager
        mediaDisplayAdapter!!.setOnDeleteIconListener(object : ItemDeleteListener {
            override fun deleteIconClicked(position: Int) {
                realEstate.photos.removeAt(position)
                mediaDisplayAdapter!!.notifyDataSetChanged()
            }
        })
        mediaRecyclerView!!.adapter = mediaDisplayAdapter
    }


    //Open MainActivity with clear task flag.
    private fun goToMainActivity() {
        val intent = Intent(this@NavigationActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val TAG = "NavigationActivity"
    }
}
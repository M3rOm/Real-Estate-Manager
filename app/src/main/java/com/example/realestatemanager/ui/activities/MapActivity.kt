package com.example.realestatemanager.ui.activities

import android.Manifest.permission
import android.R.drawable
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.clustering.ClusterManager
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string
import com.example.realestatemanager.model.ClusterMarker
import com.example.realestatemanager.model.RealEstate
import com.example.realestatemanager.repository.Repository
import com.example.realestatemanager.utils.ClustersRenderer
import com.example.realestatemanager.utils.Constants.MapsCodes
import com.example.realestatemanager.utils.Utils
import com.example.realestatemanager.utils.Utils.OnReceivingBitmapFromUrl
import java.util.ArrayList

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mLocationPermissionGranted = false
    private var mMapView: MapView? = null
    private var mGoogleMap: GoogleMap? = null
    private var mUserPosition: GeoPoint? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var mClusterManagerRenderer: ClustersRenderer? = null
    private var bitmapList: List<Bitmap>? = null
    private var allListings: LiveData<List<RealEstate>>? = null
    private val mClusterMarkers = ArrayList<ClusterMarker>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_map)
        setToolbar()
        if (Utils.isInternetAvailable(this@MapActivity)) {
            initMap(savedInstanceState)
        } else {
            Toast.makeText(
                baseContext, "No wifi internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initMap(savedInstanceState: Bundle?) {
        val repository =
            Repository(baseContext)
        allListings = repository.allListings
        allListings?.observe(this, Observer { realEstates ->
            val photosUrls: MutableList<String> = ArrayList()
            for ((_, _, _, _, _, _, _, photos) in realEstates) {
                photosUrls.add(photos[0])
            }
            Utils.bitmapsFromUrl(
                photosUrls,
                object : OnReceivingBitmapFromUrl{
                    override fun onSuccess(bitmaps: List<Bitmap>) {
                        bitmapList = bitmaps
                        addMapMarkers()
                    }

                    override fun onFailure(e: Exception) {}
                },
                this@MapActivity
            )
        })
        mLocationPermissionGranted = checkMapServices()
        mMapView = findViewById(id.activity_map_map_view)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lastKnownLocation
        initGoogleMap(savedInstanceState)
    }

    private fun addMapMarkers() {
        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager = ClusterManager(applicationContext, mGoogleMap)
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = ClustersRenderer(
                    this,
                    mGoogleMap,
                    mClusterManager
                )
                mClusterManager?.renderer = mClusterManagerRenderer
            }
            if (allListings!!.value != null) {
                for (i in allListings?.value?.indices!!) {
                    try {
                        val realEstate = allListings!!.value!![i]
                        val address =
                            Utils.getAddressClassFromString(
                                realEstate.address,
                                baseContext
                            )
                        var avatar: Bitmap? = null
                        try {
                            avatar = bitmapList!![i]
                        } catch (ignored: NumberFormatException) {
                        }
                        val newClusterMarker = address?.longitude?.let {
                            LatLng(
                                address.latitude,
                                it
                            )
                        }?.let {
                            ClusterMarker(
                                it,
                                realEstate.type,
                                realEstate.description,
                                avatar!!
                            )
                        }
                        mClusterManager?.addItem(newClusterMarker)
                        newClusterMarker?.let { mClusterMarkers.add(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            mClusterManager?.cluster()
        }
    }

    private val lastKnownLocation: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions =
                    arrayOf(permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    DEVICE_PERMISSION_REQUEST_CODE
                )
                return
            }
            mFusedLocationClient!!.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val location = task.result
                        if (location != null) {
                            mUserPosition = GeoPoint(location.latitude, location.longitude)
                        }
                        if (mGoogleMap != null) {
                            setCameraView()
                        }
                    }
                }
        }

    private fun setCameraView() {
        try { // Set a boundary to start
            val bottomBoundary = mUserPosition!!.latitude - MAP_SCOPE
            val leftBoundary = mUserPosition!!.longitude - MAP_SCOPE
            val topBoundary = mUserPosition!!.latitude + MAP_SCOPE
            val rightBoundary = mUserPosition!!.longitude + MAP_SCOPE
            val mMapBoundary = LatLngBounds(
                LatLng(bottomBoundary, leftBoundary),
                LatLng(topBoundary, rightBoundary)
            )
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToolbar() {
        val toolbar =
            findViewById<Toolbar>(id.map_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MapsCodes.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MapsCodes.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MapsCodes.MAPVIEW_BUNDLE_KEY)
        }
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)
    }

    private fun checkMapServices(): Boolean {
        return if (isServicesOK) {
            isMapsEnabled
        } else false
    }

    private fun buildAlertMessageNoGps() {
        val builder = Builder(this)
        builder.setMessage(
            "This application requires GPS to work properly" +
                    ", do you want to enable it?"
        )
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val enableGpsIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivityForResult(enableGpsIntent, MapsCodes.PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        val alert = builder.create()
        alert.show()
    }

    val isMapsEnabled: Boolean
        get() {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
                return false
            }
            return true
        }

    private val locationPermission: Unit
        get() {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
                showMap()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(permission.ACCESS_FINE_LOCATION),
                    MapsCodes.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }

    private fun showMap() {
        Toast.makeText(baseContext, "Show Map", Toast.LENGTH_SHORT).show()
    }

    val isServicesOK: Boolean
        get() {
            val available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(baseContext)
            if (available == ConnectionResult.SUCCESS) {
                return true
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                val dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(
                        this@MapActivity, available,
                        MapsCodes.ERROR_DIALOG_REQUEST
                    )
                dialog.show()
            }
            return false
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        if (requestCode == MapsCodes.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
            } else {
                showDontHavePermissionsDialog()
            }
        } else if (requestCode == DEVICE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                restartMapActivity()
            } else {
                showDontHavePermissionsDialog()
            }
        }
    }

    private fun showDontHavePermissionsDialog() {
        Builder(this@MapActivity)
            .setTitle(getString(string.we_cant_show_the_map))
            .setMessage(getString(string.no_map_permissions))
            .setPositiveButton(getString(string.ok)) { dialog, which ->
                dialog.dismiss()
                finish()
            }
            .setIcon(drawable.ic_dialog_alert)
            .show()
    }

    private fun restartMapActivity() {
        val intent = Intent(this@MapActivity, MapActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MapsCodes.PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                showMap()
            } else {
                locationPermission
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mMapView != null) mMapView?.onResume()
    }

    public override fun onStart() {
        super.onStart()
        if (mMapView != null) mMapView?.onStart()
    }

    public override fun onStop() {
        super.onStop()
        if (mMapView != null) mMapView?.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this@MapActivity
                , permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this@MapActivity
                , permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true
        mGoogleMap = map
        if (mUserPosition != null) {
            setCameraView()
        }
    }

    public override fun onPause() {
        if (mMapView != null) mMapView?.onPause()
        super.onPause()
    }

    public override fun onDestroy() {
        if (mMapView != null) mMapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (mMapView != null) mMapView?.onLowMemory()
    }

    companion object {
        private const val DEVICE_PERMISSION_REQUEST_CODE = 12
        private val TAG = MapActivity::class.java.simpleName
        private const val MAP_SCOPE = 0.07
    }
}
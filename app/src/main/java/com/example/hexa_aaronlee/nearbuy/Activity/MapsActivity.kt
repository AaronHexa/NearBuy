package com.example.hexa_aaronlee.nearbuy.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.Adapter.PlaceAutocompleteAdapter
import com.example.hexa_aaronlee.nearbuy.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException


class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var client: GoogleApiClient
    private lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mDataRef: DatabaseReference

    private lateinit var locationRequest: LocationRequest
    lateinit var locationMain: Location
    var currentMarker: Marker? = null
    lateinit var latLng: LatLng

    lateinit var mAutocompleteAdapter: PlaceAutocompleteAdapter
    var LAT_LNG_BOUNDS: LatLngBounds = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))

    val REQUEST_LOCATION_CODE = 99
    var result: FloatArray = FloatArray(10)
    var arrayMarker: ArrayList<Marker> = ArrayList()
    lateinit var saleArray: ArrayList<String>
    lateinit var userIdArray: ArrayList<String>
    lateinit var offerIdArray: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        saleArray = ArrayList()
        userIdArray = ArrayList()
        offerIdArray = ArrayList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.v2
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
            mMap.setOnInfoWindowClickListener(this)
        }

        searchTxt.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    || event.action == KeyEvent.KEYCODE_ENTER) {
                geoLocate()
                true
            } else {
                false
            }
        }

        icon_gps.setOnClickListener {

            for (i in arrayMarker.indices) { //remove the marker on map
                arrayMarker[i].remove()
            }

            bulidGoogleApiClient() // rebuild Client to get Current Location
        }

        scan_ic.setOnClickListener {
            getAllUserId()
        }


        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        mAutocompleteAdapter = PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null)
        searchTxt.setAdapter(mAutocompleteAdapter)

        mMap.uiSettings.isMyLocationButtonEnabled = false

        HidSoftKeyboard()
    }

    @Synchronized
    protected fun bulidGoogleApiClient() {
        client = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
        client.connect()

    }

    override fun onConnected(p0: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this)
        }


    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        locationMain = location

        val latitude = location.latitude
        val longitude = location.longitude

        UserDetail.mLatitude = latitude
        UserDetail.mLongitude = longitude

        latLng = LatLng(latitude, longitude)

        moveCamera(latLng, "My Location")

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        }

        HidSoftKeyboard()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (client == null) {
                        bulidGoogleApiClient()
                    }
                    mMap.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);
            }
            false

        } else
            true
    }

    fun geoLocate() {
        val textSearch = searchTxt.text.toString()

        val geocoder = Geocoder(applicationContext)
        var list: List<Address> = ArrayList()

        try {
            list = geocoder.getFromLocationName(textSearch, 1)
        } catch (e: IOException) {
            Log.e("MapsActivity", "geolocate : IOException" + e.message)
        }

        if (list.isNotEmpty()) {
            val address: Address = list[0]

            Log.e("MapsActivity", "geolocate : Found a location : " + address.toString())

            moveCamera(LatLng(address.latitude, address.longitude), address.getAddressLine(0))


            /*
            val address = addresses.get(0).getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses.get(0).getLocality()
            val state = addresses.get(0).getAdminArea()
            val country = addresses.get(0).getCountryName()
            val postalCode = addresses.get(0).getPostalCode()
            */
        }
    }

    fun moveCamera(latLng: LatLng, title: String) {

        if (currentMarker != null) {
            currentMarker!!.remove()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        val options: MarkerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
        currentMarker = mMap.addMarker(options)

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        getAllUserId()
        HidSoftKeyboard()
    }

    fun getAllUserId(){

        userIdArray = ArrayList()

        mDataRef = FirebaseDatabase.getInstance().reference.child("User")

        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: UserData = dataSnapshot.getValue(UserData::class.java)!!
                userIdArray.add(data.user_id)
                Log.i("Count : ",userIdArray.count().toString())
                goGetDistanceData(userIdArray,(userIdArray.count()-1))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun goGetDistanceData(userIdArray: ArrayList<String>,countNum : Int) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(userIdArray[countNum])

        saleArray = ArrayList()

        mDataRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.getValue(DealsDetailData::class.java)
                if (map != null) {
                    if (map.offer_id == userIdArray[countNum]) {
                        Location.distanceBetween(UserDetail.mLatitude, UserDetail.mLongitude, map.mLatitude.toDouble(), map.mLongitude.toDouble(), result)
                        val tmpDistance = String.format("%.2f", (result[0] / 1000))

                        if (result[0] <= 3000) {

                            Log.i("Location ... : ", "${map.mLongitude}........${map.mLatitude}....${result[0]} ")

                            val latLng = LatLng(map.mLatitude.toDouble(), map.mLongitude.toDouble())

                            val options: MarkerOptions = MarkerOptions()
                                    .position(latLng)
                                    .title("<${map.itemTitle}> ${map.itemLocation}")
                            arrayMarker.add(mMap.addMarker(options))

                            saleArray.add(map.sales_id)
                            offerIdArray.add(map.offer_id)
                        }
                    }
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val map = dataSnapshot.getValue(DealsDetailData::class.java)
                if (map != null) {
                    if (map.offer_id == userIdArray[countNum]) {
                        Location.distanceBetween(UserDetail.mLatitude, UserDetail.mLongitude, map.mLatitude.toDouble(), map.mLongitude.toDouble(), result)
                        val tmpDistance = String.format("%.2f", (result[0] / 1000))

                        if (result[0] <= 3000) {

                            Log.i("Location ... : ","${map.mLongitude}........${map.mLatitude}....${result[0]} ")

                            val latLng = LatLng(map.mLatitude.toDouble(), map.mLongitude.toDouble())

                            val options: MarkerOptions = MarkerOptions()
                                    .position(latLng)
                                    .title("<${map.itemTitle}> ${map.itemLocation}")
                            arrayMarker.add(mMap.addMarker(options))

                            saleArray.add(map.sales_id)
                            offerIdArray.add(map.offer_id)
                        }

                    }
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
            }
        })
    }

    override fun onInfoWindowClick(p0: Marker?) {
        for (i in arrayMarker.indices) {
            if (p0 == arrayMarker[i]) {

                val intent = Intent(applicationContext, ViewSaleDetailsActivity::class.java)

                intent.putExtra("saleID",saleArray[i])
                intent.putExtra("offerID",offerIdArray[i])

                startActivity(intent)
            }
        }
    }

    fun HidSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onBackPressed() {
        //startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
    }
}

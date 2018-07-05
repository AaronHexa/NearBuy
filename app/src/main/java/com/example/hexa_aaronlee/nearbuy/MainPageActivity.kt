package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.Presenter.MainPagePresenter
import com.example.hexa_aaronlee.nearbuy.View.MainPageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.navigate_header.*
import kotlinx.android.synthetic.main.navigate_header.view.*


class MainPageActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        MainPageView.View,
        GoogleMap.OnInfoWindowClickListener,
        NavigationView.OnNavigationItemSelectedListener{
    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     *
     * @return true to display the item as the selected item
     */

    private lateinit var mMap: GoogleMap
    private lateinit var client: GoogleApiClient
    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var locationRequest: LocationRequest
    lateinit var locationMain: Location
    var currentMarker: Marker? = null
    lateinit var latLng: LatLng

    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0

    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    lateinit var geocoder: Geocoder

    var personName: String = ""
    var personEmail: String = ""
    var user_id: String = ""

    val REQUEST_LOCATION_CODE = 99
    lateinit var mPresenter: MainPagePresenter

    var arrayMarker: ArrayList<Marker> = ArrayList()
    lateinit var saleArray: ArrayList<String>
    lateinit var userIdArray: ArrayList<String>
    lateinit var offerIdArray: ArrayList<String>
    lateinit var lstSaleData: ArrayList<DealsDetailData>
    lateinit var lstUserId: ArrayList<String>

    //drawer layout
    lateinit var mDrawerLayout : DrawerLayout
    lateinit var mTonggle : ActionBarDrawerToggle

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
            mMap.setOnInfoWindowClickListener(this)
        }


        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()
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

        mLatitude = latitude
        mLongitude = longitude

        UserDetail.mLatitude = latitude
        UserDetail.mLongitude = longitude

        latLng = LatLng(latitude, longitude)


        mPresenter.moveCamera(latLng, "My Location", mMap)

        //get Area Sale Detail
        userIdArray = ArrayList()
        offerIdArray = ArrayList()
        saleArray = ArrayList()

        mPresenter.getAreaSaleDetail(arrayMarker, saleArray, userIdArray, offerIdArray, mMap, mLongitude, mLatitude)

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        }

        geocoder = Geocoder(this)

        mPresenter.getAddress(geocoder, latitude, longitude)

    }

    override fun displayLocationAddress(address: String) {
        currentLocationTxt.text = address
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

    override fun setUpUIMarker(arrayMarker: ArrayList<Marker>, saleArray: ArrayList<String>, offerIdArray: ArrayList<String>) {
        this.arrayMarker = arrayMarker
        this.saleArray = saleArray
        this.offerIdArray = offerIdArray
    }


    override fun onInfoWindowClick(p0: Marker?) {
        for (i in arrayMarker.indices) {
            if (p0 == arrayMarker[i]) {
                Log.i("Location : ", saleArray[i])
                UserDetail.saleSelectedId = saleArray[i]
                UserDetail.saleSelectedUserId = offerIdArray[i]
                startActivity(Intent(applicationContext, ViewSaleDetailsActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mAuth = FirebaseAuth.getInstance()

        user_id = mAuth.currentUser!!.uid
        UserDetail.user_id = user_id


        mainActivityScrollView.post({
            mainActivityScrollView.fullScroll(View.FOCUS_UP)
        })

        mPresenter = MainPagePresenter(this)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        setUINotScrollable()
        setDrawerNavi()

        mPresenter.getUserDataFromDatabase(user_id)

        mapLayout.setOnClickListener { startActivity(Intent(applicationContext, MapsActivity::class.java)) }

        setSaleArrayList()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.historyChat_Nav ->{
                startActivity(Intent(applicationContext, ChatHistoryActivity::class.java))
                Log.i("Clicked : ", "Yes")
            }
            R.id.createSale_Nav ->{
                startActivity(Intent(applicationContext, CreateSaleActivity::class.java))
            }
            R.id.showAllSale_Nav ->{
                startActivity(Intent(applicationContext, ShowTotalSalesActivity::class.java))
            }
            R.id.profile_Nav ->{
                startActivity(Intent(applicationContext, ProfileInfoActivity::class.java))
            }
            R.id.map_Nav ->{
                startActivity(Intent(applicationContext, MapsActivity::class.java))
            }
            R.id.logout_Nav ->{
                onBackPressed()
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setDrawerNavi(){
        mDrawerLayout = findViewById(R.id.drawerLayout)
        mTonggle = ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close)

        mTonggle.isDrawerIndicatorEnabled = true
        mDrawerLayout.addDrawerListener(mTonggle)
        mTonggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val navMenu = findViewById<NavigationView>(R.id.nav_Menu)

        navMenu.setNavigationItemSelectedListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mTonggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setUINotScrollable(){
        transparent_image.setOnTouchListener({ v: View?, event: MotionEvent? ->
            val action = event!!.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_UP -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener false
                }
                else -> return@setOnTouchListener true
            }

        })

        transparent_image2.setOnTouchListener({ v: View?, event: MotionEvent? ->
            val action = event!!.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_UP -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    mainActivityScrollView.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener false
                }
                else -> return@setOnTouchListener true
            }

        })
    }

    fun setSaleArrayList() {

        lstSaleData = ArrayList()
        lstUserId = ArrayList()

        mPresenter.getAllUserID(lstUserId)

        switchUI.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                lstSaleData = ArrayList()

                for (i in lstUserId.indices) {
                    mPresenter.getSaleData(lstSaleData, lstUserId[i])
                }
            } else if (isChecked) {
                lstSaleData = ArrayList()

                for (i in lstUserId.indices) {
                    mPresenter.getSaleDataWithLimitDistance(lstSaleData, UserDetail.mLatitude, UserDetail.mLongitude, lstUserId[i])
                }
            }


        }

    }

    override fun setLoopCheckSale(lstUserId: ArrayList<String>) {
        for (i in 0 until lstUserId.count() - 1) {
            mPresenter.getSaleData(lstSaleData, lstUserId[i])
        }
    }

    override fun updateList(lstSaleData: ArrayList<DealsDetailData>) {
        val myrv:RecyclerView = findViewById(R.id.mainRecyclerView)
        val myAdapter = RecyclerViewAdapter(this, lstSaleData)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }


    override fun setUserDataToView(email: String, name: String, profilePic: String) {
        personName = name
        personEmail = email

        UserDetail.email = email
        UserDetail.username = name
        UserDetail.imageUrl = profilePic

        setNavHeaderText(name)

        setUIUpdate()
    }

    fun setNavHeaderText(name : String){
        val navigationView = findViewById<NavigationView>(R.id.nav_Menu)
        val headerView = navigationView.getHeaderView(0)
        headerView.navName.text = "Welcome "+ name
    }

    fun setUIUpdate() {
        nameTxt.text = personName
        emailTxt.text = personEmail

        Picasso.get()
                .load(Uri.parse(UserDetail.imageUrl))
                .resize(700, 700)
                .centerCrop()
                .into(userImage)
    }

    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this)

        builder.setIcon(R.drawable.ic_power_settings_new_black_24dp)
        builder.setTitle("Logout")
        builder.setMessage("Are You Sure You Want Logout?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes", { dialog, whichButton ->

            //Sign out from Firebase Auth
            FirebaseAuth.getInstance().signOut()

            //Sign out From Google
            mGoogleSignInClient.signOut()
            startActivity(Intent(applicationContext, LoginMainActivity::class.java))
            finish()

            dialog.dismiss()
        })

        builder.setNegativeButton("Cancel", { dialog, whichButton ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }
}

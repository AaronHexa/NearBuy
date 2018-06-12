package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main_page.*
import java.io.IOException


class MainPage : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var client: GoogleApiClient
    private lateinit var mGoogleApiClient :GoogleApiClient

    private lateinit var locationRequest: LocationRequest
    lateinit var locationMain: Location
    var currentMarker: Marker? = null
    lateinit var latLng: LatLng

    var mLatitude :Double = 0.0
    var mLongitude : Double = 0.0

    private lateinit var mAuth : FirebaseAuth
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    lateinit var databaseR : DatabaseReference

    var personName : String? = null
    var personEmail : String?= null
    var user_id : String= ""

    val REQUEST_LOCATION_CODE = 99

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
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

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this)
        }


    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        locationMain = location

        var latitude = location.latitude
        var longitude = location.longitude

        mLatitude = latitude
        mLongitude = longitude

        UserDetail.mLatitude = latitude
        UserDetail.mLongitude = longitude

        latLng = LatLng(latitude, longitude)

        moveCamera(latLng,"My Location")

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        }

        var geocoder = Geocoder(applicationContext)
        var list : List<Address> = ArrayList()

        try {
            list = geocoder.getFromLocation(latitude,longitude,1)
        }catch (e : IOException)
        {
            Log.e("MapsActivity","geolocate : IOException" + e.message)
        }

        if(list.isNotEmpty())
        {
            var address: Address = list[0]

            currentLocationTxt.text = address.getAddressLine(0)

            Log.e("MapsActivity","geolocate : Found a location : " + address.toString())
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == REQUEST_LOCATION_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                {
                    if(client == null)
                    {
                        bulidGoogleApiClient()
                    }
                    mMap.isMyLocationEnabled = false
                }
            }
            else
            {
                Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);
            }
            return false

        } else
            return true
    }

    fun moveCamera(latLng: LatLng, title : String)
    {

        if (currentMarker != null) {
            currentMarker!!.remove()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f))

        val options : MarkerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
        currentMarker = mMap.addMarker(options)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(16f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mAuth = FirebaseAuth.getInstance()

        user_id = mAuth.currentUser!!.uid
        UserDetail.user_id = user_id

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        signOutBtn.setOnClickListener{

            //Sign out from Firebase Auth
            FirebaseAuth.getInstance().signOut()

            //Sign out From Google
            mGoogleSignInClient.signOut()

            startActivity(Intent(applicationContext,LoginMain::class.java))
            finish()
        }

        historyBtn.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,ChatHistory::class.java)) }

        profileBtn.setOnClickListener{
            finish()
            startActivity(Intent(applicationContext,ProfileInfo::class.java))
        }

        mapBtn.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,MapsActivity::class.java))
        }

        dealBtn.setOnClickListener{
            finish()
            startActivity(Intent(applicationContext,CreateSale::class.java))
        }

        totalSalesBtn.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,ShowTotalSales::class.java))
        }

        getUserDataFromDatabase()
    }


    fun getUserDataFromDatabase()
    {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)!!
                personName = data.name
                personEmail = data.email

                UserDetail.email = data.email
                UserDetail.username = data.name
                UserDetail.imageUrl = data.profilePhoto

                System.out.println("..................."+UserDetail.username+" "+ UserDetail.user_id)

                setUIUpdate()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun setUIUpdate()
    {
        nameTxt.text = personName
        emailTxt.text = personEmail

        Picasso.get()
                .load(Uri.parse(UserDetail.imageUrl))
                .resize(700,700)
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
            startActivity(Intent(applicationContext, LoginMain::class.java))
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

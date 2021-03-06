package com.example.hexa_aaronlee.nearbuy.Activity

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import android.widget.Toolbar
import com.example.hexa_aaronlee.nearbuy.Adapter.PlaceAutocompleteAdapter
import com.example.hexa_aaronlee.nearbuy.Presenter.CreateSalePresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.CreateSaleView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_sale.*
import kotlinx.android.synthetic.main.activity_profile_info.*


class CreateSaleActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CreateSaleView.View {

    // ...............................Google Map Parts.............................................

    private lateinit var mMap: GoogleMap
    private lateinit var client: GoogleApiClient
    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var locationRequest: LocationRequest
    lateinit var locationMain: Location
    var currentMarker: Marker? = null
    lateinit var markerOptions: MarkerOptions
    lateinit var latLng: LatLng

    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0

    lateinit var mAutocompleteAdapter: PlaceAutocompleteAdapter
    var LAT_LNG_BOUNDS: LatLngBounds = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))

    val REQUEST_LOCATION_CODE = 99

    lateinit var mPresenter: CreateSalePresenter

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }

        locationSelectionTxt.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    || event.action == KeyEvent.KEYCODE_ENTER) {
                val textSearch = locationSelectionTxt.text.toString()
                mPresenter.geoLocate(textSearch, applicationContext)
                true
            } else {
                false
            }
        }

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        mAutocompleteAdapter = PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null)
        locationSelectionTxt.setAdapter(mAutocompleteAdapter)

        //...........Add item Onclick for map search..............

        locationSelectionTxt.setOnItemClickListener { parent, view, position, id ->
            HidSoftKeyboard()
            val item = mAutocompleteAdapter.getItem(position)!!
            val placeId = item.placeId

            val placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId)
            placeResult.setResultCallback({
                if (!it.status.isSuccess) {
                    Log.i("TAG", "onResult: Place query did not complete successfully: " + it.status.toString())
                    it.release()
                }
                val place = it.get(0)

                Log.i("Latitude : ", place.latLng.toString())
                mPresenter.moveCameraAfterSelection(place.latLng,place.address.toString(),mMap,this,place.name.toString())
            })
        }

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

        mLatitude = latitude
        mLongitude = longitude

        UserDetail.mLatitude = latitude
        UserDetail.mLongitude = longitude

        mPresenter.moveCamera(latitude, longitude, "My Location", mMap, applicationContext)


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
                    mMap.isMyLocationEnabled = false
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
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

    override fun getLatLngSetCamera(latitude: Double, longitude: Double, address: String) {
        mLatitude = latitude
        mLongitude = longitude

        mPresenter.moveCamera(latitude, longitude, address, mMap, applicationContext)
    }

    override fun setMarker(currentMarker: Marker, address: String) {
        if (this.currentMarker != null) {
            this.currentMarker?.remove()
        }

        this.currentMarker = currentMarker
        UserDetail.currentAddress = address

        Log.i("MapsActivity ",UserDetail.currentAddress)

        HidSoftKeyboard()
    }

    override fun SetLatLng(latitude: Double, longitude: Double) {
        mLatitude = latitude
        mLongitude = longitude
    }

    fun HidSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }



    //...............................Information Parts..............................................



    val PICK_IMAGE_REQUEST = 1
    lateinit var filePath: Uri
    lateinit var view: View
    var salesId: String = ""
    var id: String = ""

    lateinit var databaseR: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    var imageSelected = 0

    var tmpTitle = ""
    var tmpDescription = ""
    var tmpPrice = ""
    var tmpDecPrice = ""
    var tmpLocation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sale)

        mPresenter = CreateSalePresenter(this)
        progressDialog = ProgressDialog(this)

        val actionBar = this.supportActionBar!!

        actionBar.title = "Deal Creation"
        actionBar.setDisplayHomeAsUpEnabled(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapSelection) as SupportMapFragment
        mapFragment.getMapAsync(this)

        databaseR = FirebaseDatabase.getInstance().reference.child("SaleDetail")

        salesId = databaseR.push().key.toString()

        itemPic1.setOnClickListener {

            choosePicture()

        }

        transparent_imagess.setOnTouchListener({ v: View?, event: MotionEvent? ->
            val action = event!!.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    scrollViewCreateActivity.requestDisallowInterceptTouchEvent(true)
                     false
                }

                MotionEvent.ACTION_UP -> {
                    scrollViewCreateActivity.requestDisallowInterceptTouchEvent(false)
                     true
                }
                MotionEvent.ACTION_MOVE -> {
                    scrollViewCreateActivity.requestDisallowInterceptTouchEvent(true)
                     false
                }
                else ->  true
            }

        })

        createBtn.setOnClickListener {
            //displaying a progress dialog

            tmpTitle = String()
            tmpPrice = String()

            tmpTitle = titleTxt.text.trim().toString()
            tmpDescription = editTxtDescription.text.trim().toString()
            tmpPrice = priceTxt.text.trim().toString()

            mPresenter.checkFillUpText(tmpTitle,tmpPrice)
        }

        cancelCreateBtn.setOnClickListener {
            finish()
        }

        createSaleLayout.setOnClickListener {
            hideKeyboard()
        }


        

    }

    fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                imageSelected = 1
                if (data!!.data != null) {
                    filePath = data.data

                    Picasso.get()
                            .load(filePath)
                            .resize(700, 700)
                            .centerCrop()
                            .into(itemPic1)

                    Toast.makeText(this, "Single", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun setLocation(tmpLocation: String, imageData1: String) {

        var newDecPrice = ""

       if (tmpDecPrice.substring(0,1) == "0"){
           newDecPrice = tmpDecPrice.substring(1)
        }
        else{
           newDecPrice = tmpDecPrice

        }

        mPresenter.saveSaleData(tmpTitle, newDecPrice, tmpDescription, tmpLocation, mLatitude.toString(), mLongitude.toString(), UserDetail.username, salesId, imageData1, UserDetail.user_id)
    }

    override fun UpdateTitleAlertUI(emptyTxt : Boolean) {
        if (emptyTxt){
            titleAlert.visibility = View.VISIBLE
        }else if (!emptyTxt){
            titleAlert.visibility = View.INVISIBLE
        }

    }

    override fun UpdatePriceAlertUI(emptyTxt : Boolean) {

        if (emptyTxt){
            priceAlert.visibility = View.VISIBLE
        }else if (!emptyTxt){
            priceAlert.visibility = View.INVISIBLE
        }

    }

    override fun AllowSaveData() {

        titleAlert.visibility = View.INVISIBLE
        priceAlert.visibility = View.INVISIBLE

        if(tmpPrice != "") {
            tmpDecPrice = String.format("%.2f", tmpPrice.toDouble())
        }

        progressDialog.setMessage("Uploading Please Wait...")
        progressDialog.show()

        if (imageSelected == 1) {
            mPresenter.savePicToStorage(applicationContext, filePath, salesId)
        } else if (imageSelected == 0) {
            filePath = Uri.parse("android.resource://" + applicationContext.packageName + "/drawable/gallery_ic")
            mPresenter.savePicToStorage(applicationContext, filePath, salesId)
        }
    }

    override fun imageUploadError(exception: Exception) {
        progressDialog.dismiss()
        Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
    }

    override fun imageUploadFailed() {
        progressDialog.dismiss()
        Toast.makeText(applicationContext, "File Fail To Upload  ", Toast.LENGTH_LONG).show()
    }

    override fun imageUploadSuccess(uriString: String) {

        progressDialog.dismiss()
        Toast.makeText(applicationContext, " Upload Successfully ", Toast.LENGTH_LONG).show()

        tmpLocation = locationSelectionTxt.text.trim().toString()

        mPresenter.checkLocationTxt(applicationContext, mLatitude, mLongitude, uriString, UserDetail.currentAddress, tmpLocation)
        finish()
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}



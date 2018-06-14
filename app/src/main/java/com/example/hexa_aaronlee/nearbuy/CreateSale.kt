package com.example.hexa_aaronlee.nearbuy

import android.app.ProgressDialog
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
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.example.hexa_aaronlee.nearbuy.Presenter.CreateSalePresenter
import com.example.hexa_aaronlee.nearbuy.View.CreateSaleView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_sale.*
import java.io.IOException

class CreateSale : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CreateSaleView.view {

    // ...............................Google Map Parts.............................................

    private lateinit var mMap: GoogleMap
    private lateinit var client: GoogleApiClient
    private lateinit var mGoogleApiClient :GoogleApiClient

    private lateinit var locationRequest: LocationRequest
    lateinit var locationMain: Location
    var currentMarker: Marker? = null
    lateinit var markerOptions: MarkerOptions
    lateinit var latLng: LatLng

    var mLatitude :Double = 0.0
    var mLongitude : Double = 0.0

    lateinit var mAutocompleteAdapter: PlaceAutocompleteAdapter
    var LAT_LNG_BOUNDS : LatLngBounds = LatLngBounds(LatLng(-40.0,-168.0), LatLng(71.0,136.0))

    val REQUEST_LOCATION_CODE = 99

    lateinit var mPresenter : CreateSalePresenter

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }

        locationSelectionTxt.setOnEditorActionListener() { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    || event.action == KeyEvent.KEYCODE_ENTER){
                val textSearch = locationSelectionTxt.text.toString()
                mPresenter.geoLocate(textSearch,applicationContext)
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

        mAutocompleteAdapter = PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null)
        locationSelectionTxt.setAdapter(mAutocompleteAdapter)

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

        val latitude = location.latitude
        val longitude = location.longitude

        mLatitude = latitude
        mLongitude = longitude

        UserDetail.mLatitude = latitude
        UserDetail.mLongitude = longitude

        mPresenter.moveCamera(latitude, longitude,"My Location",mMap,applicationContext)


        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        }

        HidSoftKeyboard()

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

    override fun getLatLngSetCamera(latitude: Double, longitude: Double, address: String) {
        mLatitude = latitude
        mLongitude = longitude

        mPresenter.moveCamera(latitude,longitude,address,mMap,applicationContext)
    }

    override fun setMarker(currentMarker: Marker,address: String) {
        if (this.currentMarker != null) {
            this.currentMarker?.remove()
        }

        this.currentMarker = currentMarker
        UserDetail.currentAddress = address

        HidSoftKeyboard()
    }

    fun HidSoftKeyboard()
    {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }



    //...............................Information Parts..............................................


    val PICK_IMAGE_REQUEST = 1
    lateinit var filePath: Uri
    lateinit var view: View
    var salesId :String =""
    var id : String = ""

    var uriString: String = ""
    lateinit var mStorage: FirebaseStorage
    lateinit var storageM: StorageReference
    lateinit var databaseR: DatabaseReference
    lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sale)

        mPresenter = CreateSalePresenter(this)
        progressDialog = ProgressDialog(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
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

        createBtn.setOnClickListener {

            //displaying a progress dialog

            progressDialog.setMessage("Uploading Please Wait...")
            progressDialog.show()

            mPresenter.savePicToStorage(applicationContext,filePath,salesId)

        }

        cancelCreateBtn.setOnClickListener{
            finish()
            startActivity(Intent(applicationContext,MainPage::class.java))
        }

    }

    fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
            if (data.data != null)
            {
                filePath = data.data

                Picasso.get()
                        .load(filePath)
                        .resize(700, 700)
                        .centerCrop()
                        .into(itemPic1)

                Toast.makeText(this,"Single",Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun setLocation(tmpLocation: String,imageData1 : String) {
        val tmpTitle = titleTxt.text.trim().toString()
        val tmpDescription = editTxtDescription.text.trim().toString()
        val tmpPrice = priceTxt.text.trim().toString()

        mPresenter.saveSaleData(tmpTitle,tmpPrice,tmpDescription,tmpLocation,mLatitude.toString(),mLongitude.toString(),UserDetail.username,salesId,imageData1,UserDetail.user_id)
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

        val tmpLocation = locationSelectionTxt.text.trim().toString()

        mPresenter.checkLocationTxt(applicationContext,mLatitude,mLongitude,uriString,UserDetail.currentAddress,tmpLocation)
        finish()
        startActivity(Intent(applicationContext,MainPage::class.java))
    }
}

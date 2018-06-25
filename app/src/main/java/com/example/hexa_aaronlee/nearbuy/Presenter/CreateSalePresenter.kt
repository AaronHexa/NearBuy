package com.example.hexa_aaronlee.nearbuy.Presenter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.View.CreateSaleView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

public class CreateSalePresenter(internal var view : CreateSaleView.View) : CreateSaleView.Presenter
{
    lateinit var  mStorage : StorageReference
    lateinit var databaseR : DatabaseReference

    override fun moveCamera(latitude : Double, longitude: Double, title: String,mMap : GoogleMap,context: Context) {

        val latLng = LatLng(latitude, longitude)

        val options : MarkerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
         var currentMarker = mMap.addMarker(options)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(16f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val geocoder = Geocoder(context)
        var list : List<Address> = ArrayList()

        try {
            list = geocoder.getFromLocation(latitude,longitude,1)
        }catch (e : IOException)
        {
            Log.e("MapsActivity","geolocate : IOException" + e.message)
        }

        if(list.isNotEmpty())
        {
            val address: Address = list[0]


            view.setMarker(currentMarker,address.getAddressLine(0).toString())

            Log.e("MapsActivity","geolocate : Found a location : " + address.toString())

        }
    }

    override fun geoLocate(textSearch: String, context: Context) {
        val geocoder = Geocoder(context)
        var list : List<Address> = ArrayList()

        try {
            list = geocoder.getFromLocationName(textSearch,1)
        }catch (e : IOException)
        {
            Log.e("MapsActivity","geolocate : IOException" + e.message)
        }

        if(list.isNotEmpty())
        {
            var address: Address = list[0]


            view.getLatLngSetCamera(address.latitude,address.longitude,address.getAddressLine(0).toString())

            Log.e("MapsActivity","geolocate : Found a location : " + address.toString())

        }
    }

    override fun saveSaleData(tmpTitle: String, tmpPrice: String, tmpDescription: String, tmpLocation: String, mLatitude: String, mLongitude: String, username: String, salesId: String, imageData1: String, user_id: String) {

        databaseR = FirebaseDatabase.getInstance().reference.child("SaleDetail")

        val data = DealsDetailData(tmpTitle, tmpPrice, tmpDescription, tmpLocation,mLatitude ,mLongitude, username,salesId,imageData1,user_id)

        databaseR.child(salesId).setValue(data)
    }

    override fun checkLocationTxt(context: Context , mLatitude: Double, mLongitude: Double,imageData1 : String,locationTxt : String,tmpLocation :String) {
        val geocoder2 = Geocoder(context)
        var list2 : List<Address> = ArrayList()

        try {
            list2 = geocoder2.getFromLocation(mLatitude,mLongitude,1)
        }catch (e : IOException)
        {
            Log.e("MapsActivity","geolocate : IOException" + e.message)
        }

        if(list2.isNotEmpty())
        {
            val address2: Address = list2[0]

            var setLocation = ""

            if(tmpLocation != null)
            {
                setLocation = address2.getAddressLine(0).toString()
            }
            else if (tmpLocation == null)
            {
                setLocation = locationTxt
            }
            System.out.println("..........>>>>>>>>$setLocation<<<<........")

            view.setLocation(setLocation,imageData1)
        }
    }

    override fun savePicToStorage(context: Context, filePath: Uri, salesId: String) {

        System.out.println(">>>>>..................$filePath..............<<<<<")

        mStorage = FirebaseStorage.getInstance().reference.child("SalesImage").child(salesId).child("image0")

        mStorage.putFile(filePath)
                .addOnSuccessListener {  }
                .addOnFailureListener({ exception ->
                    //if the upload is not successfull

                    //and displaying error message
                    view.imageUploadError(exception)

                })
                .continueWithTask({ task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }

                    // Continue with the task to get the download URL
                    mStorage.downloadUrl
                }).addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        println("....Download url....>>>" + downloadUri.toString())

                        view.imageUploadSuccess(downloadUri.toString())

                    } else {
                        // Handle failures
                        view.imageUploadFailed()

                    }
                })

    }

}
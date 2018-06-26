package com.example.hexa_aaronlee.nearbuy.Presenter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.UserDetail
import com.example.hexa_aaronlee.nearbuy.View.MainPageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_page.*
import java.io.IOException

public class MainPagePresenter(internal var view : MainPageView.View) : MainPageView.Presenter {

    lateinit var databaseR : DatabaseReference

    override fun moveCamera(latLng: LatLng, title: String,mMap: GoogleMap) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f))

        val options : MarkerOptions = MarkerOptions()
                .position(latLng)
                .title(title)
        mMap.addMarker(options)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(16f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun getAddress(geocoder: Geocoder, latitude: Double, longitude: Double) {

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

            view.displayLocationAddress(address.getAddressLine(0).toString())

            Log.e("MapsActivity","geolocate : Found a location : " + address.toString())
        }
    }

    override fun getUserDataFromDatabase(user_id: String) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)!!

                view.setUserDataToView(data.email,data.name,data.profilePhoto)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

}
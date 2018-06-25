package com.example.hexa_aaronlee.nearbuy.View

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

public interface MainPageView {
    interface View {
        fun displayLocationAddress(address: String)
        fun setUserDataToView(email: String, name: String, profilePic: String)
    }

    interface Presenter {
        fun moveCamera(latLng: LatLng, title: String, mMap: GoogleMap)
        fun getAddress(geocoder: Geocoder, latitude: Double, longitude: Double)
        fun getUserDataFromDatabase(user_id: String)
    }
}
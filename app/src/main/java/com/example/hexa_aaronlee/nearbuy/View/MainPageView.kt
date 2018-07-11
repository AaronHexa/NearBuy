package com.example.hexa_aaronlee.nearbuy.View

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.Presenter.LoginPresenter
import com.example.hexa_aaronlee.nearbuy.Presenter.MainPagePresenter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

public interface MainPageView {
    interface View {
        fun displayLocationAddress(address: String)
        fun setUserDataToView(email: String,
                              name: String,
                              profilePic: String)
        fun setUpUIMarker(arrayMarker: ArrayList<Marker>,
                          saleArray: ArrayList<String>,
                          offerIdArray: ArrayList<String>)

        //Sale List Display
        fun updateList(lstSaleData: ArrayList<DealsDetailData>)
        fun setLoopCheckSale(lstUserId: ArrayList<String>)
    }

    interface Presenter {
        fun moveCamera(latLng: LatLng,
                       title: String,
                       mMap: GoogleMap)

        fun getAddress(geocoder: Geocoder,
                       latitude: Double,
                       longitude: Double)

        fun getUserDataFromDatabase(user_id: String)

        fun getAreaSaleDetail(arrayMarker: ArrayList<Marker>,
                              saleArray: ArrayList<String>,
                              userIdArray: ArrayList<String>,
                              offerIdArray: ArrayList<String>,
                              mMap: GoogleMap,
                              mLongitude : Double,
                              mLatitude : Double)

        //Sale List Display
        fun getAllUserID(lstUserId: ArrayList<String>)
        fun getSaleData(lstSaleData: ArrayList<DealsDetailData>,
                        user_id: String)

        fun getSaleDataWithLimitDistance(lstSaleData: ArrayList<DealsDetailData>,
                                         mLatitude: Double,
                                         mLongitude: Double,
                                         user_id: String)
    }
}
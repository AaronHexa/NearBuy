package com.example.hexa_aaronlee.nearbuy.View

import android.content.Context
import android.net.Uri
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

public interface CreateSaleView {

    interface View {
        fun setMarker(currentMarker: Marker,
                      address: String)

        fun getLatLngSetCamera(latitude: Double,
                               longitude: Double,
                               address: String)

        fun setLocation(tmpLocation: String,
                        imageData1: String)

        fun imageUploadSuccess(uriString: String)
        fun imageUploadFailed()
        fun imageUploadError(exception: Exception)
        fun UpdateAlertUI()
        fun AllowSaveData()
    }

    interface Presenter {
        //Map function
        fun moveCamera(latitude: Double,
                       longitude: Double,
                       title: String,
                       mMap: GoogleMap,
                       context: Context)

        fun geoLocate(textSearch: String,
                      context: Context)

        //save in database
        fun saveSaleData(tmpTitle: String,
                         tmpPrice: String,
                         tmpDescription: String,
                         tmpLocation: String,
                         mLatitude: String,
                         mLongitude: String,
                         username: String,
                         salesId: String,
                         imageData1: String,
                         ser_id: String)

        fun checkLocationTxt(context: Context,
                             mLatitude: Double,
                             mLongitude: Double,
                             imageData1: String,
                             locationTxt: String,
                             tmpLocation: String)

        //save in storage
        fun savePicToStorage(context: Context,
                             filePath: Uri,
                             salesId: String)

        fun checkFillUpText(tmpTitle: String,tmpPrice: String)
    }
}
package com.example.hexa_aaronlee.nearbuy.Presenter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.UserDetail
import com.example.hexa_aaronlee.nearbuy.View.MainPageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
                .zoom(14f)                   // Sets the zoom
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

    override fun getAreaSaleDetail(arrayMarker: ArrayList<Marker>, saleArray: ArrayList<String>, userIdArray: ArrayList<String>, offerIdArray: ArrayList<String>,mMap: GoogleMap,mLongitude : Double, mLatitude :Double) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User")

        databaseR.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: UserData = dataSnapshot.getValue(UserData::class.java)!!
                userIdArray.add(data.user_id)
                Log.i("Count : ",userIdArray.count().toString())
                goGetDistanceData(userIdArray,(userIdArray.count()-1),arrayMarker,saleArray,offerIdArray,mMap,mLongitude,mLatitude)
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

    fun goGetDistanceData(userIdArray: ArrayList<String>,countNum : Int,arrayMarker: ArrayList<Marker>, saleArray: ArrayList<String>, offerIdArray: ArrayList<String>,mMap: GoogleMap,mLongitude : Double, mLatitude :Double) {
        databaseR = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(userIdArray[countNum])

        val result = FloatArray(10)

        databaseR.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val map = dataSnapshot.getValue(DealsDetailData::class.java)
                if (map != null) {
                    if (map.offer_id == userIdArray[countNum]) {
                        Location.distanceBetween(mLatitude,mLongitude, map.mLatitude.toDouble(), map.mLongitude.toDouble(), result)
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

                            view.setUpUIMarker(arrayMarker,saleArray,offerIdArray)
                        }

                    }
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
            }
        })
    }

    //..........Sale List Display.......

    lateinit var mDataRef: DatabaseReference
    var tmpCount = 0
    var totalCount = 0

    override fun getAllUserID(lstUserId: ArrayList<String>) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("User")

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                totalCount = p0.childrenCount.toInt()-1
                getUserID(lstUserId, totalCount)
            }

        })


    }

    fun getUserID(lstUserId: ArrayList<String>, totalCount: Int) {

        mDataRef = FirebaseDatabase.getInstance().reference.child("User")

        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: UserData = dataSnapshot.getValue(UserData::class.java)!!
                if (tmpCount !=totalCount){
                    lstUserId.add(data.user_id)
                    Log.i("user id :", data.user_id)
                    tmpCount++
                }
                else if (tmpCount == totalCount){
                    view.setLoopCheckSale(lstUserId)
                }
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

    override fun getSaleData(lstSaleData: ArrayList<DealsDetailData>, user_id: String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(user_id)


        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: DealsDetailData = dataSnapshot.getValue(DealsDetailData::class.java)!!
                if (data.offer_id == user_id) {
                    lstSaleData.add(DealsDetailData(data.itemTitle, data.itemPrice, data.itemDescription, data.itemLocation, data.mLatitude, data.mLongitude, data.offerBy, data.sales_id, data.sales_image1, data.offer_id))
                    view.updateList(lstSaleData)
                }

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


    override fun getSaleDataWithLimitDistance(lstSaleData: ArrayList<DealsDetailData>, mLatitude: Double, mLongitude: Double,user_id : String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(user_id)

        val result = FloatArray(10)

        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: DealsDetailData = dataSnapshot.getValue(DealsDetailData::class.java)!!
                if (data.offer_id == user_id){
                    Location.distanceBetween(mLatitude, mLongitude, data.mLatitude.toDouble(), data.mLongitude.toDouble(), result)
                    val tmpDistance = (result[0] / 1000).toDouble()
                    if (tmpDistance <= 3) {
                        lstSaleData.add(DealsDetailData(data.itemTitle, data.itemPrice, data.itemDescription, data.itemLocation, data.mLatitude, data.mLongitude, data.offerBy, data.sales_id, data.sales_image1, data.offer_id))

                        view.updateList(lstSaleData)
                    }
                }
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
}
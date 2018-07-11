package com.example.hexa_aaronlee.nearbuy.Presenter

import android.location.Location
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.ShowTotalSalesView
import com.google.firebase.database.*

class ShowTotalSalesPresenter(internal var view: ShowTotalSalesView.View) : ShowTotalSalesView.Presenter {

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
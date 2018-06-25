package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.View.ShowTotalSalesView
import com.google.firebase.database.*

class ShowTotalSalesPresenter(internal var view : ShowTotalSalesView.View) : ShowTotalSalesView.Presenter
{
    lateinit var mDataRef : DatabaseReference

    override fun getSaleData(lstSaleData: ArrayList<DealsDetailData>) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail")


        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data : DealsDetailData = dataSnapshot.getValue(DealsDetailData::class.java)!!
                lstSaleData.add(DealsDetailData(data.itemTitle,data.itemPrice,data.itemDescription,data.itemLocation,data.mLatitude,data.mLongitude,data.offerBy,data.sales_id,data.sales_image1,data.offer_id))

                view.updateList(lstSaleData)
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
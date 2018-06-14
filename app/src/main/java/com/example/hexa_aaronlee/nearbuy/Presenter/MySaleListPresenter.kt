package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.example.hexa_aaronlee.nearbuy.View.MySaleListView
import com.google.firebase.database.*

class MySaleListPresenter(internal var view : MySaleListView.View) : MySaleListView.Presenter
{
    lateinit var mDataRef : DatabaseReference

    override fun getSaledata(user_id: String, lstDetail: ArrayList<DealsDetail>) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail")


        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data : DealsDetail = dataSnapshot.getValue(DealsDetail::class.java)!!

                if(data.offer_id == user_id)
                {
                    lstDetail.add(DealsDetail(data.itemTitle,data.itemPrice,data.itemDescription,data.itemLocation,data.mLatitude,data.mLongitude,data.offerBy,data.sales_id,data.sales_image1,data.offer_id))
                    view.updateList(lstDetail)
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
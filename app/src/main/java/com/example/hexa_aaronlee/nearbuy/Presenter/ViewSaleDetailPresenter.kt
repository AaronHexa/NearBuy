package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.ViewSaleDetailView
import com.google.firebase.database.*

class ViewSaleDetailPresenter(internal var view : ViewSaleDetailView.View) : ViewSaleDetailView.Presenter
{
    lateinit var mDataRef : DatabaseReference
    lateinit var mDataRef2 : DatabaseReference

    override fun getSalesDetail(saleSelectedId: String,saleOfferId : String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(saleOfferId).child(saleSelectedId)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(DealsDetailData::class.java)!!

                view.updateUI(data.sales_image1,data.itemTitle,data.itemPrice,data.itemDescription,data.itemLocation,data.mLatitude,data.mLongitude,data.offerBy,data.offer_id)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    override fun getChatDetail(checkDealer : String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("User").child(checkDealer)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserData::class.java)!!

                view.updateInfo(data.profilePhoto,data.name,data.user_id)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }
    override fun saveUserToHistoryChat(profilePhoto: String,user_id : String, checkDealer: String, username : String , tmpSaleTitle :String, saleSelectedId: String,dealerName : String,dealerPic : String) {

        mDataRef = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)

        mDataRef2 = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(checkDealer)



        val data = HistoryData(checkDealer,saleSelectedId,dealerName,dealerPic,tmpSaleTitle,"New",1) // save for user

        val data2 = HistoryData(user_id,saleSelectedId,username,profilePhoto,tmpSaleTitle,"New",1) // save for dealer

        mDataRef.child(saleSelectedId).setValue(data)
        mDataRef2.child(saleSelectedId).setValue(data2)
    }


    override fun checkHistorySaleData(saleSelectedId: String,user_id: String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id).child(saleSelectedId)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists())
                {
                    Log.i("Got Data : ", " ..in TotalHistory.... Yes")
                    view.saveHistoryData(false)
                }
                else if (!dataSnapshot.exists())
                {
                    Log.i("Got Data : ", " ..in TotalHistory.... No")
                    view.saveHistoryData(true)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }
}
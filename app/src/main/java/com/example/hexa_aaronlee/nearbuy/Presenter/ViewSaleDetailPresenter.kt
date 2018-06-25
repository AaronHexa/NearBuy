package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.ViewSaleDetailView
import com.google.firebase.database.*

class ViewSaleDetailPresenter(internal var view : ViewSaleDetailView.View) : ViewSaleDetailView.Presenter
{

    lateinit var mDataRef : DatabaseReference

    override fun getSalesDetail(saleSelectedId: String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(saleSelectedId)

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

                view.updateInfo(data.profilePhoto,data.name)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }
    override fun saveUserToHistoryChat(profilePhoto: String,user_id : String, checkDealer: String, tmpSaleUser : String , tmpSaleTitle :String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)

        var tmpKey = mDataRef.push().key.toString()

        var data = HistoryData(checkDealer,tmpKey,tmpSaleUser,profilePhoto,tmpSaleTitle)

        mDataRef.child(tmpKey).setValue(data)
    }

}
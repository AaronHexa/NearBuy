package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_sale_details.*

class ViewSaleDetails : AppCompatActivity() {

    lateinit var mDataRef : DatabaseReference
    lateinit var imageUri : Uri
    var tmpSaleTitle : String = ""
    var tmpSaleUser : String = ""
    var checkDealer : String = ""
    var result : FloatArray = FloatArray(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sale_details)

        getSaleDetail()

        floatingActionButton.setOnClickListener {
            if(checkDealer == UserDetail.user_id)
            {
                Toast.makeText(application,"You are the dealer !!!",Toast.LENGTH_SHORT).show()
            }
            else
            {
                UserDetail.chatWithID = checkDealer
                getSaleChatData()
                finish()
                startActivity(Intent(applicationContext,ChatRoom::class.java))
            }

        }
    }

    fun getSaleDetail()
    {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(UserDetail.saleSelectedId)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(DealsDetail::class.java)!!

                imageUri = Uri.parse(data.sales_image1)

                Picasso.get()
                        .load(imageUri)
                        .centerCrop()
                        .resize(700,700)
                        .into(imageProduct)

                titleDetail.text = data.itemTitle
                priceDetail.text = "MYR "+ data.itemPrice
                descripeDetail.text = data.itemDescription
                locationDetail.text = data.itemLocation
                Location.distanceBetween(UserDetail.mLatitude,UserDetail.mLongitude,data.mLatitude.toDouble(),data.mLongitude.toDouble(),result)
                distanceDetail.text = String.format("%.2f",(result[0]/1000)) + " km"
                offeredDetail2.text = data.offerBy

                checkDealer = data.offer_id
                tmpSaleTitle = data.itemTitle
                tmpSaleUser = data.offerBy
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun getSaleChatData()
    {

        mDataRef = FirebaseDatabase.getInstance().reference.child("User").child(checkDealer)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserData::class.java)!!

                UserDetail.chatWithImageUri = data.profilePhoto
                UserDetail.chatWithName = data.name
                saveUserToHistoryChat(data.profilePhoto)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun saveUserToHistoryChat(imageUri : String)
    {
        mDataRef = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(UserDetail.user_id)

        var tmpKey = mDataRef.push().key.toString()

        var data = HistoryData(checkDealer,tmpKey,tmpSaleUser,imageUri,tmpSaleTitle)

        mDataRef.child(tmpKey).setValue(data)
    }

    override fun onBackPressed() {

        finish()
        startActivity(Intent(applicationContext,MainPage::class.java))
        super.onBackPressed()
    }
}

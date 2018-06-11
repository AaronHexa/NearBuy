package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_sale_details.*

class ViewSaleDetails : AppCompatActivity() {

    lateinit var mDataRef : DatabaseReference
    lateinit var imageUri : Uri
    var result : FloatArray = FloatArray(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sale_details)

        getSaleDetail()

        floatingActionButton.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,ChatHistory::class.java))
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
                distanceDetail.text = result[0].toString() + " m"
                offeredDetail2.text = data.offerBy
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    override fun onBackPressed() {

        finish()
        startActivity(Intent(applicationContext,MainPage::class.java))
        super.onBackPressed()
    }
}

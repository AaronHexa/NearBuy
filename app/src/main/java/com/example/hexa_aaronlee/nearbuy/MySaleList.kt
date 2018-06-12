package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.google.firebase.database.*

class MySaleList : AppCompatActivity() {

    lateinit var mDataRef : DatabaseReference

    lateinit var lstBook : ArrayList<DealsDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sale_list)

        lstBook = ArrayList()

        getSaleData()
    }
    fun getSaleData()
    {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail")


        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data : DealsDetail = dataSnapshot.getValue(DealsDetail::class.java)!!

                if(data.offer_id == UserDetail.user_id)
                {
                    lstBook.add(DealsDetail(data.itemTitle,data.itemPrice,data.itemDescription,data.itemLocation,data.mLatitude,data.mLongitude,data.offerBy,data.sales_id,data.sales_image1,data.offer_id))
                    updateList()
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

    fun updateList()
    {
        val myrv = findViewById<RecyclerView>(R.id.listMySale)
        val myAdapter = RecyclerViewAdapter("700",this, lstBook)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, ProfileInfo::class.java))
        finish()
    }
}

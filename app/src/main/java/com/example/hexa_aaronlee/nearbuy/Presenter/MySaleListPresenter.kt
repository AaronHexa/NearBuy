package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.View.MySaleListView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MySaleListPresenter(internal var view: MySaleListView.View) : MySaleListView.Presenter {

    lateinit var mDataRef: DatabaseReference
    lateinit var mStorageRef: StorageReference

    override fun checkSaleData(user_id: String, lstDetail: ArrayList<DealsDetailData>) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(user_id)

        mDataRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("Read Failed : ", error.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    getDataSale(user_id, lstDetail)
                } else {
                    Log.i("Check Data :", " No Data")
                    view.updateList(lstDetail)
                }
            }

        })


    }

    fun getDataSale(user_id: String, lstDetail: ArrayList<DealsDetailData>) {

        var newListDetail : ArrayList<DealsDetailData>
        newListDetail = ArrayList()

        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(user_id)

        mDataRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data: DealsDetailData = dataSnapshot.getValue(DealsDetailData::class.java)!!

                if (data.offer_id == user_id) {

                    newListDetail.add(DealsDetailData(data.itemTitle, data.itemPrice, data.itemDescription, data.itemLocation, data.mLatitude, data.mLongitude, data.offerBy, data.sales_id, data.sales_image1, data.offer_id))
                    view.updateList(newListDetail)
                    view.setDeleteBtn(newListDetail)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val data: DealsDetailData = dataSnapshot.getValue(DealsDetailData::class.java)!!

                if (data.offer_id == user_id) {

                    newListDetail.add(DealsDetailData(data.itemTitle, data.itemPrice, data.itemDescription, data.itemLocation, data.mLatitude, data.mLongitude, data.offerBy, data.sales_id, data.sales_image1, data.offer_id))
                    view.updateList(newListDetail)
                    view.setDeleteBtn(newListDetail)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    override fun deleteSaleInDatabase(mDeletionPos: ArrayList<Int>, lstDetail: ArrayList<DealsDetailData>, user_id: String) {
        mDataRef = FirebaseDatabase.getInstance().reference.child("SaleDetail")
        mStorageRef = FirebaseStorage.getInstance().reference.child("SalesImage")


        for (i in mDeletionPos.indices) {
            mDataRef.child(lstDetail[mDeletionPos[i]].offer_id).child(lstDetail[mDeletionPos[i]].sales_id).removeValue()
            mStorageRef.child(lstDetail[mDeletionPos[i]].sales_id).child("image0").delete()
        }

        val newLstDetailData: ArrayList<DealsDetailData> = ArrayList()
        checkSaleData(user_id, newLstDetailData)

        view.FinishDeletion()
    }
}
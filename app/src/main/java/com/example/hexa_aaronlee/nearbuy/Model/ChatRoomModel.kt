package com.example.hexa_aaronlee.nearbuy.Model

import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.*

class ChatRoomModel {

    fun checkHistoryFlowable(user_id: String, chatListKey: String): Flowable<ArrayList<HistoryData>> {
        val newDataList = ArrayList<HistoryData>()
        return Flowable.create({ emitter ->
            val databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id).child(chatListKey)
            val valueListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if (!emitter.isCancelled) {
                        emitter.onError(p0.toException())
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val data = dataSnapshot.getValue(HistoryData::class.java)
                    data?.let {
                        newDataList.add(it)
                    }
                    emitter.onNext(newDataList)
                }
            }
            // cleanup if subscription cancelled
            emitter.setCancellable {
                databaseR.removeEventListener(valueListener)
                emitter.onComplete()
            }
            databaseR.addListenerForSingleValueEvent(valueListener)
        }, BackpressureStrategy.BUFFER)
    }

     fun getUserStatusCountFlowable(chatUserId: String ,chatListKey: String) : Flowable<ArrayList<HistoryData>>{
         val newDataList = ArrayList<HistoryData>()
         return Flowable.create({ emitter ->
             val databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(chatUserId).child(chatListKey)
             val valueListener = object : ValueEventListener {
                 override fun onCancelled(p0: DatabaseError) {
                     if (!emitter.isCancelled) {
                         emitter.onError(p0.toException())
                     }
                 }

                 override fun onDataChange(dataSnapshot: DataSnapshot) {

                     val data = dataSnapshot.getValue(HistoryData::class.java)
                     data?.let {
                         newDataList.add(it)
                     }
                     emitter.onNext(newDataList)
                 }
             }
             // cleanup if subscription cancelled
             emitter.setCancellable {
                 databaseR.removeEventListener(valueListener)
                 emitter.onComplete()
             }
             databaseR.addListenerForSingleValueEvent(valueListener)
         }, BackpressureStrategy.BUFFER)
     }
}
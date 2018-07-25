package com.example.hexa_aaronlee.nearbuy.Model

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class ChatHistoryModel {

    fun getTotalHistoryFlowable(user_id : String): Flowable<ArrayList<HistoryData>> {

        val newDataList = ArrayList<HistoryData>()
        // Create a RX stream to listen for callback and return the result as Flowable
        // when the callback returned
        return Flowable.create({ emitter ->
            val databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)
            val valueListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if (!emitter.isCancelled) {
                        emitter.onError(p0.toException())
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        val data = postSnapshot.getValue(HistoryData::class.java)
                        data?.let {
                            newDataList.add(it)
                        }
                    }
                    emitter.onNext(newDataList)
                    emitter.onComplete()
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
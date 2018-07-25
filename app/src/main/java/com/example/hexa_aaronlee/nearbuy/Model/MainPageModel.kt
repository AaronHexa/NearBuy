package com.example.hexa_aaronlee.nearbuy.Model

import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class MainPageModel {

    fun getUserDataFlowable(user_id : String): Flowable<ArrayList<UserData>> {
        val newDataList = ArrayList<UserData>()
        return Flowable.create({ emitter ->
            val databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)
            val valueListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if (!emitter.isCancelled) {
                        emitter.onError(p0.toException())
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val data = dataSnapshot.getValue(UserData::class.java)
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
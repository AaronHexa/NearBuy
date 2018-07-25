package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.Model.ChatHistoryModel
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*
import io.reactivex.FlowableSubscriber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription

class ChatHistoryPresenter(internal var view: ChatHistoryView.View) : ChatHistoryView.Presenter {

    lateinit var databaseR: DatabaseReference
    lateinit var newDataList: ArrayList<HistoryData>

    val mModel = ChatHistoryModel()

    override fun getHistory(user_id: String) {

        mModel.getTotalHistoryFlowable(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : FlowableSubscriber<ArrayList<HistoryData>> {
                    override fun onComplete() {
                        Log.i("Get History", "Done")
                    }

                    override fun onSubscribe(s: Subscription) {
                        //Need to request subscriber (because flowableSubscriber extend subcriber)
                        s.request(Long.MAX_VALUE)
                    }

                    override fun onNext(dataList: ArrayList<HistoryData>) {
                        checkMsgsStatus(dataList)
                        Log.i("data is",dataList.toString())
                    }

                    override fun onError(t: Throwable?) {
                        Log.e("Get History", t!!.message.toString())
                    }

                })
    }

    fun checkMsgsStatus(dataList: ArrayList<HistoryData>) {
        newDataList = ArrayList()
        val newMsg = ArrayList<Int>()
        val oldMsg = ArrayList<Int>()

        for (i in 0 until (dataList.count() + 1)) {
            if (i != dataList.count()) {
                if (dataList[i].msg_status == "New") {
                    newMsg.add(i)
                    Log.i("new i : ", i.toString())
                } else if (dataList[i].msg_status == "Old") {
                    oldMsg.add(i)
                    Log.i("old i : ", i.toString())
                }
            } else if (i == dataList.count()) {
                for (count in 0 until 2) {
                    if (count != 1) {
                        for (x in newMsg.indices) {
                            newDataList.add(dataList[newMsg[x]])
                        }

                        for (y in oldMsg.indices) {
                            newDataList.add(dataList[oldMsg[y]])

                        }

                    } else if (count == 1) {
                        view.setRecyclerViewAdapter(newDataList)
                    }
                }

            }
        }
    }

    override fun checkChatHistiryData(user_id: String) {

        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)

        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("Got Data : ", " ...for Chat History Activity... Yes")
                    view.setEmptyViewAdapter(true)
                } else {
                    Log.i("Got Data : ", " ...for Chat History Activity... NO")
                    view.setEmptyViewAdapter(false)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }


}
package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*

class ChatHistoryPresenter(internal var view: ChatHistoryView.View) : ChatHistoryView.Presenter {

    lateinit var databaseR: DatabaseReference
    lateinit var newDataList: ArrayList<HistoryData>

    override fun getChatHistoryDataFromDatabase(dataList: ArrayList<HistoryData>, user_id: String) {

        var tmpNum = 0
        var tmpString = ""

        newDataList = ArrayList()

        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)!!

                    newDataList.add(HistoryData(data.history_user, data.sale_id, data.history_userName, data.history_image, data.history_title, data.msg_status, data.msg_statusCount, data.chatListKey))

                    Log.i("HistoryUser :", " ${data.sale_id}")

                    tmpNum += 1
                    tmpString = tmpNum.toString()

                    if (tmpString == dataSnapshot.childrenCount.toString()) {
                        checkMsgStatus(dataList)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)

            }
        })
    }

    fun checkMsgStatus(dataList: ArrayList<HistoryData>) {
        val newMsg = ArrayList<Int>()
        val oldMsg = ArrayList<Int>()

        for (i in 0 until (newDataList.count() + 1)) {
            if (i != newDataList.count()) {
                if (newDataList[i].msg_status == "New") {
                    newMsg.add(i)
                    Log.i("new i : ", i.toString())
                } else if (newDataList[i].msg_status == "Old") {
                    oldMsg.add(i)
                    Log.i("old i : ", i.toString())
                }
            } else if (i == newDataList.count()) {
                for (count in 0 until 2) {
                    if (count != 1) {
                        for (x in newMsg.indices) {
                            dataList.add(newDataList[newMsg[x]])
                        }

                        for (y in oldMsg.indices) {
                            dataList.add(newDataList[oldMsg[y]])

                        }

                    } else if (count == 1) {
                        view.setRecyclerViewAdapter(dataList)
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
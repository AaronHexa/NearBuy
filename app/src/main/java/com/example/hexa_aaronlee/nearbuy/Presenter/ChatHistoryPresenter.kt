package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*

class ChatHistoryPresenter(internal var view: ChatHistoryView.View) : ChatHistoryView.Presenter {

    lateinit var databaseR: DatabaseReference
    lateinit var newHistoryData: ArrayList<String>
    lateinit var newImageData: ArrayList<String>
    lateinit var newNameData: ArrayList<String>
    lateinit var newTitleData: ArrayList<String>
    lateinit var newSaleData: ArrayList<String>
    lateinit var newMsgStatus: ArrayList<String>
    lateinit var newMsgStatusCount: ArrayList<Int>

    override fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>, user_id: String, saleData: ArrayList<String>, msg_status: ArrayList<String>, msg_statusCount: ArrayList<Int>) {
        var tmpNum = 0
        var tmpString = ""

        newHistoryData = ArrayList()
        newImageData = ArrayList()
        newNameData = ArrayList()
        newTitleData = ArrayList()
        newSaleData = ArrayList()
        newMsgStatus = ArrayList()
        newMsgStatusCount = ArrayList()

        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)!!

                    newHistoryData.add(data.history_user)
                    newNameData.add(data.history_userName)
                    newImageData.add(data.history_image)
                    newTitleData.add(data.history_title)
                    newSaleData.add(data.sale_id)
                    newMsgStatus.add(data.msg_status)
                    newMsgStatusCount.add(data.msg_statusCount)

                    Log.i("HistoryUser :", " ${data.msg_statusCount}")

                    tmpNum += 1
                    tmpString = tmpNum.toString()

                    if (tmpString == dataSnapshot.childrenCount.toString()) {
                        checkMsgStatus(historyData, imageData, nameData, titleData, saleData, msg_status, msg_statusCount)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)

            }
        })
    }

    fun checkMsgStatus(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>, saleData: ArrayList<String>, msg_status: ArrayList<String>, msg_statusCount: ArrayList<Int>) {
        val newMsg = ArrayList<Int>()
        val oldMsg = ArrayList<Int>()

        for (i in 0 until (newMsgStatus.count() + 1)) {
            if (i != newMsgStatus.count()) {
                if (newMsgStatus[i] == "New") {
                    newMsg.add(i)
                    Log.i("new i : ", i.toString())
                } else if (newMsgStatus[i] == "Old") {
                    oldMsg.add(i)
                    Log.i("old i : ", i.toString())
                }
            } else if (i == newMsgStatus.count()) {
                for (count in 0 until 2) {
                    if (count != 1) {
                        for (x in newMsg.indices) {
                            historyData.add(newHistoryData[newMsg[x]])
                            imageData.add(newImageData[newMsg[x]])
                            nameData.add(newNameData[newMsg[x]])
                            titleData.add(newTitleData[newMsg[x]])
                            saleData.add(newSaleData[newMsg[x]])
                            msg_status.add(newMsgStatus[newMsg[x]])
                            msg_statusCount.add(newMsgStatusCount[newMsg[x]])
                        }

                        for (y in oldMsg.indices) {
                            historyData.add(newHistoryData[oldMsg[y]])
                            imageData.add(newImageData[oldMsg[y]])
                            nameData.add(newNameData[oldMsg[y]])
                            titleData.add(newTitleData[oldMsg[y]])
                            saleData.add(newSaleData[oldMsg[y]])
                            msg_status.add(newMsgStatus[oldMsg[y]])
                            msg_statusCount.add(newMsgStatusCount[oldMsg[y]])

                        }

                    } else if (count == 1) {
                        view.setRecyclerViewAdapter(historyData, imageData, nameData, titleData, saleData, msg_status, msg_statusCount)
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
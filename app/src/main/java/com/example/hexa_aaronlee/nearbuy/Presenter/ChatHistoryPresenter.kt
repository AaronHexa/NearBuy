package com.example.hexa_aaronlee.nearbuy.Presenter

import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*

class ChatHistoryPresenter(internal var view: ChatHistoryView.View) : ChatHistoryView.Presenter {

    lateinit var databaseR: DatabaseReference

    override fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>, user_id: String, saleData: ArrayList<String>, chatDate: ArrayList<String>, chatTime: ArrayList<String>) {
        var tmpNum = 0
        var tmpString = ""

        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)!!

                    historyData.add(data.history_user)
                    nameData.add(data.history_userName)
                    imageData.add(data.history_image)
                    titleData.add(data.history_title)
                    saleData.add(data.sale_id)
                    chatDate.add(data.created_date)
                    chatTime.add(data.created_time)

                    Log.i("HistoryUser :", " ${data.history_user}")

                    tmpNum += 1
                    tmpString = tmpNum.toString()

                    if (tmpString == dataSnapshot.childrenCount.toString()) {
                        view.setRecyclerViewAdapter(historyData, imageData, nameData, titleData, saleData, chatDate, chatTime)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)

            }
        })
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
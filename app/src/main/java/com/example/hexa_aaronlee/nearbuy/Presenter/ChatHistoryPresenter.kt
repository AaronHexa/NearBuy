package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*

public class ChatHistoryPresenter(internal var view : ChatHistoryView.view) : ChatHistoryView.presenter
{
    lateinit var databaseR: DatabaseReference

    override fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>, user_id: String) {
        var tmpNum: Int = 0
        var tmpString: String = ""

        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)!!
                    historyData.add(data.history_user)
                    nameData.add(data.history_userName)
                    imageData.add(data.history_image)
                    titleData.add(data.history_title)

                    System.out.println("..................." + data.history_user)

                    tmpNum += 1
                    tmpString = tmpNum.toString()
                    System.out.println(tmpString + "............" + dataSnapshot.childrenCount.toString())

                    if (tmpString.equals(dataSnapshot.childrenCount.toString())) {

                        view.setRecyclerViewAdapter(historyData,imageData,nameData,titleData)
                        System.out.println(tmpNum)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

}
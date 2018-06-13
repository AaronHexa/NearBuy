package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.google.firebase.database.DatabaseReference

public class ChatRoomPresenter(internal var view : ChatRoomView.view) : ChatRoomView.presenter
{
    override fun saveChatMsg(messageText: String, user_id: String, arrayMsgIDList: ArrayList<String>, newMessagePage: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var databaseRef: DatabaseReference
    lateinit var databaseRef2: DatabaseReference

}
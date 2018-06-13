package com.example.hexa_aaronlee.nearbuy.View

public interface ChatRoomView{

    interface view{
        fun setEditTextEmpty()
    }

    interface presenter{
        fun saveChatMsg(messageText: String,user_id: String,arrayMsgIDList : ArrayList<String>, newMessagePage: Int)
    }
}
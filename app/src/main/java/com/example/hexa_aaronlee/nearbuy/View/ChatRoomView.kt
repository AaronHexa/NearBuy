package com.example.hexa_aaronlee.nearbuy.View

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout

public interface ChatRoomView{

    interface view{
        fun setEditTextEmpty()
        fun uploadImageSuccess()
        fun uploadImageFailed()
        fun saveImageData(uriTxt : String)
        fun addMsgChat(newMessagePage : Int, imageFileName: String,text: String, sender: String, type :String,arrayMsgIDList: ArrayList<String>)
        fun viewLargeImage(tmpUri: Uri)
    }

    interface presenter{
        fun saveChatMsg(messageText: String,user_id: String,arrayMsgIDList : ArrayList<String>, newMessagePage: Int,selectedUser : String)
        fun savePicMsg(uriTxt: String,user_id: String,arrayMsgIDList : ArrayList<String>, newMessagePage: Int,selectedUser : String)
        fun comfrimationPicSend(newMessagePage: Int, user_id: String, selectedUser: String, filePath: Uri, imageFileName : String, dialog: DialogInterface)
        fun createMsgBubble(text: String, sender: String, type: String, context: Context, user_id :String, lp2 : LinearLayout.LayoutParams, layout1 : LinearLayout)
        fun retrieveMsgData (user_id :String, selectedUser: String, arrayMsgIDList: ArrayList<String>)
    }
}
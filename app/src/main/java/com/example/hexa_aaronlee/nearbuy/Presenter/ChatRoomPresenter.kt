package com.example.hexa_aaronlee.nearbuy.Presenter

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.MessageData
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

public class ChatRoomPresenter(internal var view : ChatRoomView.view) : ChatRoomView.presenter {

    lateinit var databaseRef: DatabaseReference
    lateinit var databaseRef2: DatabaseReference
    lateinit var mStorage: FirebaseStorage

    override fun saveChatMsg(messageText: String, user_id: String, arrayMsgIDList: ArrayList<String>, newMessagePage: Int, selectedUser: String) {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")



        if (newMessagePage == 0) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val num = 1

                println("....................>>>$num")

                map["messageText"] = messageText
                map["userSend"] = user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Text"

                databaseRef.child(user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(selectedUser).child(user_id).child(num.toString()).setValue(map)

                view.setEditTextEmpty()

            }
        } else if (newMessagePage == 1) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val i = arrayMsgIDList.get(arrayMsgIDList.size.minus(1))
                val num = Integer.parseInt(i) + 1

                println("....................>>>$num")

                map["messageText"] = messageText
                map["userSend"] = user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Text"

                databaseRef.child(user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(selectedUser).child(user_id).child(num.toString()).setValue(map)

                view.setEditTextEmpty()
            }
        }
    }

    override fun savePicMsg(uriTxt: String, user_id: String, arrayMsgIDList: ArrayList<String>, newMessagePage: Int, selectedUser: String) {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")


        if (newMessagePage == 0) {

            val map = HashMap<String, String>()

            val num = 1

            println("....................>>>$num")

            map["messageText"] = uriTxt
            map["userSend"] = user_id
            map["message_id"] = num.toString()
            map["msg_type"] = "Picture"

            databaseRef.child(user_id).child(selectedUser).child(num.toString()).setValue(map)
            databaseRef2.child(selectedUser).child(user_id).child(num.toString()).setValue(map)

            view.setEditTextEmpty()


        } else if (newMessagePage == 1) {

            val map = HashMap<String, String>()

            val i = arrayMsgIDList.get(arrayMsgIDList.size.minus(1))
            val num = Integer.parseInt(i) + 1

            println("....................>>>$num")

            map["messageText"] = uriTxt
            map["userSend"] = user_id
            map["message_id"] = num.toString()
            map["msg_type"] = "Picture"

            databaseRef.child(user_id).child(selectedUser).child(num.toString()).setValue(map)
            databaseRef2.child(selectedUser).child(user_id).child(num.toString()).setValue(map)

            view.setEditTextEmpty()

        }
    }

    override fun comfrimationPicSend(newMessagePage: Int, user_id: String, selectedUser: String, filePath: Uri, imageFileName: String, dialog: DialogInterface) {
        var tmpFileName: String = ""

        if (newMessagePage == 1) {
            tmpFileName = ((imageFileName.toInt()) + 1).toString()
        } else if (newMessagePage == 0) {
            tmpFileName = Integer.valueOf("1").toString()
        }


        mStorage = FirebaseStorage.getInstance()
        var mReference = mStorage.reference.child("PictureSent").child(user_id).child(selectedUser).child(tmpFileName)
        try {
            mReference.putFile(filePath).addOnSuccessListener {

                view.uploadImageSuccess()

            }.continueWithTask({ task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                // Continue with the task to get the download URL
                mReference.downloadUrl
            }).addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    println("....Download url....>>>" + downloadUri.toString())
                    view.saveImageData(downloadUri.toString())

                } else {
                    view.uploadImageFailed()
                }
            })
        } catch (e: Exception) {
        }

        dialog.dismiss()
    }

    override fun createMsgBubble(text: String, sender: String, type: String, context: Context,user_id :String,lp2 :LinearLayout.LayoutParams,layout1 :LinearLayout) {
        if(type == "Text")
        {
            val textView = TextView(context)
            textView.text = text
            textView.textSize = 20f
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.maxLines = 100

            if (sender == user_id) {
                lp2.gravity = Gravity.END
                lp2.topMargin = 13
                textView.gravity = Gravity.CENTER or Gravity.START
                textView.setBackgroundResource(R.drawable.speech_bubble2)

            } else {
                lp2.gravity = Gravity.START
                lp2.topMargin = 18
                textView.gravity = Gravity.CENTER or Gravity.START
                textView.setBackgroundResource(R.drawable.speech_bubble)
            }

            textView.layoutParams = lp2
            layout1.addView(textView)
        }
        else if (type == "Picture")
        {
            val tmpUri = Uri.parse(text)
            val imageView = ImageView(context)

            if (sender == user_id) {
                lp2.gravity = Gravity.RIGHT
                lp2.topMargin = 10
                imageView.setBackgroundResource(R.drawable.speech_bubble2)

            } else {
                lp2.gravity = Gravity.LEFT
                lp2.topMargin = 15
                imageView.setBackgroundResource(R.drawable.speech_bubble)
            }

            imageView.layoutParams = lp2
            layout1.addView(imageView)
            System.out.println("...>>>>>>....$tmpUri")
            Picasso.get()
                    .load(tmpUri)
                    .resize(900, 900)
                    .centerCrop()
                    .into(imageView)
        }
    }

    override fun retrieveMsgData(user_id: String, selectedUser: String, arrayMsgIDList: ArrayList<String>) {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History").child(user_id).child(selectedUser)


        databaseRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                var newMessagePage = 1 as Int
                val map = dataSnapshot.getValue(MessageData::class.java)

                if (map != null) {
                    var imageFileName = map.message_id
                    arrayMsgIDList.add(map.message_id)

                    println("----->>>> " + arrayMsgIDList.size)

                    view.addMsgChat(newMessagePage,imageFileName,map.messageText, map.userSend, map.msg_type,arrayMsgIDList)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}

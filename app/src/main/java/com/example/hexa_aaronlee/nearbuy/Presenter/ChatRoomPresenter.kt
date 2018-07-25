package com.example.hexa_aaronlee.nearbuy.Presenter

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.MessageData
import com.example.hexa_aaronlee.nearbuy.Model.ChatRoomModel
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import io.reactivex.FlowableSubscriber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription


public class ChatRoomPresenter(internal var view: ChatRoomView.View) : ChatRoomView.Presenter {

    lateinit var databaseRef: DatabaseReference
    lateinit var databaseRef2: DatabaseReference
    lateinit var mStorage: FirebaseStorage
    var historyUser: String = ""
    var saleId: String = ""
    var history_userName: String = ""
    var history_image: String = ""
    var history_title: String = ""
    var msg_statusCount: Int = 0
    var historyChatUser: String = ""
    var historyChatUserName: String = ""
    var historyChatImage: String = ""
    var mModel = ChatRoomModel()

    override fun checkHistoryData(user_id: String, chatListKey: String,chatWithUser:String) {
        mModel.checkHistoryFlowable(user_id,chatListKey)
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

                        historyUser = dataList[0].history_user
                        saleId = dataList[0].sale_id
                        history_userName = dataList[0].history_userName
                        history_image = dataList[0].history_image
                        history_title = dataList[0].history_title

                        getChatUserStatusCount(chatWithUser,chatListKey)
                    }

                    override fun onError(t: Throwable?) {
                        Log.e("Get History", t!!.message.toString())
                    }

                })
    }

    fun getChatUserStatusCount(chatUserId : String ,chatListKey: String){

        mModel.getUserStatusCountFlowable(chatUserId,chatListKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : FlowableSubscriber<ArrayList<HistoryData>> {
                    override fun onComplete() {
                        Log.i("Get", "User Status Count Done")
                    }

                    override fun onSubscribe(s: Subscription) {
                        //Need to request subscriber (because flowableSubscriber extend subcriber)
                        s.request(Long.MAX_VALUE)
                    }

                    override fun onNext(dataList: ArrayList<HistoryData>) {

                        historyChatUser = dataList[0].history_user
                        historyChatUserName = dataList[0].history_userName
                        historyChatImage = dataList[0].history_image
                        msg_statusCount = dataList[0].msg_statusCount
                        history_title =dataList[0].history_title
                    }

                    override fun onError(t: Throwable?) {
                        Log.e("Got Error", t!!.message.toString())
                    }

                })
    }

    override fun saveMsgStatus(user_id: String, chatListKey: String,chatWithUser:String) {

        databaseRef = FirebaseDatabase.getInstance().reference.child("TotalHistory")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("TotalHistory")

        msg_statusCount += 1

        val data = HistoryData(historyUser, saleId, history_userName, history_image, history_title, "Old", 0,chatListKey)
        val data2 = HistoryData(historyChatUser, saleId, historyChatUserName, historyChatImage, history_title, "New", msg_statusCount,chatListKey)

        databaseRef.child(user_id).child(chatListKey).setValue(data)
        databaseRef2.child(chatWithUser).child(chatListKey).setValue(data2)

    }

    override fun saveChatMsg(messageText: String, user_id: String, arrayMsgIDList: ArrayList<String>, newMessagePage: Int, selectedUser: String, sale_id: String,currentTime:String, currentDate: String) {

        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")



        if (newMessagePage == 0) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val num = 1


                map["messageText"] = messageText
                map["userSend"] = user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Text"
                map["msgTime"] = currentTime
                map["msgDate"] = currentDate

                databaseRef.child(user_id).child(selectedUser).child(sale_id).child(num.toString()).setValue(map)
                databaseRef2.child(selectedUser).child(user_id).child(sale_id).child(num.toString()).setValue(map)

                view.setEditTextEmpty()

            }
        } else if (newMessagePage == 1) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val i = arrayMsgIDList[arrayMsgIDList.size.minus(1)]
                val num = Integer.parseInt(i) + 1


                map["messageText"] = messageText
                map["userSend"] = user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Text"
                map["msgTime"] = currentTime
                map["msgDate"] = currentDate

                databaseRef.child(user_id).child(selectedUser).child(sale_id).child(num.toString()).setValue(map)
                databaseRef2.child(selectedUser).child(user_id).child(sale_id).child(num.toString()).setValue(map)

                view.setEditTextEmpty()
            }
        }
    }

    override fun savePicMsg(uriTxt: String, user_id: String, arrayMsgIDList: ArrayList<String>, newMessagePage: Int, selectedUser: String, sale_id: String,currentTime:String, currentDate: String) {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")


        if (newMessagePage == 0) {

            val map = HashMap<String, String>()

            val num = 1


            map["messageText"] = uriTxt
            map["userSend"] = user_id
            map["message_id"] = num.toString()
            map["msg_type"] = "Picture"
            map["msgTime"] = currentTime
            map["msgDate"] = currentDate

            databaseRef.child(user_id).child(selectedUser).child(sale_id).child(num.toString()).setValue(map)
            databaseRef2.child(selectedUser).child(user_id).child(sale_id).child(num.toString()).setValue(map)

            view.setEditTextEmpty()


        } else if (newMessagePage == 1) {

            val map = HashMap<String, String>()

            val i = arrayMsgIDList.get(arrayMsgIDList.size.minus(1))
            val num = Integer.parseInt(i) + 1


            map["messageText"] = uriTxt
            map["userSend"] = user_id
            map["message_id"] = num.toString()
            map["msg_type"] = "Picture"
            map["msgTime"] = currentTime
            map["msgDate"] = currentDate

            databaseRef.child(user_id).child(selectedUser).child(sale_id).child(num.toString()).setValue(map)
            databaseRef2.child(selectedUser).child(user_id).child(sale_id).child(num.toString()).setValue(map)

            view.setEditTextEmpty()

        }
    }

    override fun comfrimationPicSend(newMessagePage: Int, user_id: String, selectedUser: String, filePath: Uri, imageFileName: String, dialog: DialogInterface, sale_id: String) {
        var tmpFileName: String = ""

        if (newMessagePage == 1) {
            tmpFileName = ((imageFileName.toInt()) + 1).toString()
        } else if (newMessagePage == 0) {
            tmpFileName = Integer.valueOf("1").toString()
        }


        mStorage = FirebaseStorage.getInstance()
        val mReference = mStorage.reference.child("PictureSent").child(user_id).child(selectedUser).child(sale_id).child(tmpFileName)
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

                    view.saveImageData(downloadUri.toString())

                } else {
                    view.uploadImageFailed()
                }
            })
        } catch (e: Exception) {
        }

        dialog.dismiss()
    }

    override fun createMsgBubble(text: String, sender: String, type: String, context: Context, user_id: String, lp2: LinearLayout.LayoutParams, layout1: LinearLayout, msgTime : String, msgDate : String, chatWithUsername: String,username:String) {

        val layoutChatBox = LinearLayout(context)
        layoutChatBox.orientation = LinearLayout.VERTICAL
        val lpChatBox = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        val timeChat = TextView(context) //gravity bottom or start/end
        timeChat.textSize = 10f
        val lpTC = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        val senderName = TextView(context) //gravity top or start/end
        senderName.textSize = 12f
        senderName.typeface = Typeface.DEFAULT_BOLD
        val lpSN = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        if (type == "Text") {
            val chatText = TextView(context)
            chatText.text = text
            chatText.textSize = 20f
            chatText.typeface = Typeface.DEFAULT_BOLD
            chatText.maxLines = 100

            if (sender == user_id) {
                lpChatBox.gravity = Gravity.END
                lpChatBox.topMargin = 5
                lp2.gravity = Gravity.CENTER or Gravity.START
                layoutChatBox.setBackgroundResource(R.drawable.chatbox)
                chatText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

                lpTC.gravity = Gravity.BOTTOM or Gravity.END
                timeChat.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                timeChat.text = msgTime

                lpSN.gravity = Gravity.TOP or Gravity.START
                senderName.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                senderName.text = username

            } else {
                lpChatBox.gravity = Gravity.START
                lpChatBox.topMargin = 18
                lp2.gravity = Gravity.CENTER or Gravity.START
                layoutChatBox.setBackgroundResource(R.drawable.chatbox)
                chatText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

                lpTC.gravity = Gravity.BOTTOM or Gravity.END
                timeChat.setTextColor(ContextCompat.getColor(context, R.color.coloOrange))
                timeChat.text = msgTime

                lpSN.gravity = Gravity.TOP or Gravity.START
                senderName.setTextColor(ContextCompat.getColor(context, R.color.coloOrange))

                senderName.text = chatWithUsername
            }

            layoutChatBox.layoutParams = lpChatBox
            chatText.layoutParams = lp2
            timeChat.layoutParams = lpTC
            senderName.layoutParams = lpSN

            layoutChatBox.addView(senderName)
            layoutChatBox.addView(chatText)
            layoutChatBox.addView(timeChat)

            layout1.addView(layoutChatBox)

        } else if (type == "Picture") {
            val tmpUri = Uri.parse(text)
            val imageView = ImageView(context)

            val lpImage=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)

            if (sender == user_id) {
                lpChatBox.gravity = Gravity.END
                lpChatBox.topMargin = 10
                layoutChatBox.setBackgroundResource(R.drawable.chatbox)

                lpTC.gravity = Gravity.BOTTOM or Gravity.END
                timeChat.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                timeChat.text = msgTime

                lpSN.gravity = Gravity.TOP or Gravity.START
                senderName.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                senderName.text = username

            } else {
                lpChatBox.gravity = Gravity.START
                lpChatBox.topMargin = 15

                layoutChatBox.setBackgroundResource(R.drawable.chatbox)

                lpTC.gravity = Gravity.BOTTOM or Gravity.END
                timeChat.setTextColor(ContextCompat.getColor(context, R.color.coloOrange))
                timeChat.text = msgTime

                lpSN.gravity = Gravity.TOP or Gravity.START
                senderName.setTextColor(ContextCompat.getColor(context, R.color.coloOrange))
                senderName.text = chatWithUsername
            }

            layoutChatBox.layoutParams = lpChatBox
            imageView.layoutParams = lpImage
            timeChat.layoutParams = lpTC
            senderName.layoutParams = lpSN

            layoutChatBox.addView(senderName)
            layoutChatBox.addView(imageView)
            layoutChatBox.addView(timeChat)


            layout1.addView(layoutChatBox)

            Picasso.get()
                    .load(tmpUri)
                    .resize(900, 900)
                    .centerCrop()
                    .into(imageView)

            imageView.setOnClickListener {
                view.viewLargeImage(tmpUri)
            }

        }
    }

    override fun retrieveMsgData(user_id: String, selectedUser: String, arrayMsgIDList: ArrayList<String>, sale_id: String) {

        databaseRef = FirebaseDatabase.getInstance().reference.child("History").child(user_id).child(selectedUser).child(sale_id)


        databaseRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val newMessagePage = 1
                val map = dataSnapshot.getValue(MessageData::class.java)
                if (map != null) {
                    val imageFileName = map.message_id
                    arrayMsgIDList.add(map.message_id)

                    view.addMsgChat(newMessagePage, imageFileName, map.messageText, map.userSend, map.msg_type, arrayMsgIDList,map.msgTime,map.msgDate)
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

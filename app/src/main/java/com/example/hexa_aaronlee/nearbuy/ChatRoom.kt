package com.example.hexa_aaronlee.nearbuy

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.hexa_aaronlee.nearbuy.DatabaseData.MessageData
import com.example.hexa_aaronlee.nearbuy.R.drawable.send_btn
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.message_area.*
import java.util.ArrayList
import java.util.HashMap

class ChatRoom : AppCompatActivity() {

    private var selectedUser: String = ""
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRef2:DatabaseReference

    private var arrayMsgIDList: ArrayList<String>? = null
    private var newMessagePage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        selectedUser = UserDetail.chatWithID
        newMessagePage = 0

        chatName.text = UserDetail.chatWithName

        System.out.println(selectedUser)

        getDataMessage()

        sendButton.setOnClickListener { saveMeassageData()}

        backFromChat.setOnClickListener{
            startActivity(Intent(applicationContext, ChatHistory::class.java))
            finish()
        }

        layout1.setOnClickListener{ v -> hideKeyboard(v) }

        messageArea.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        setting_clip.setOnClickListener{ }
    }

    fun saveMeassageData() {

        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")

        val messageText = messageArea.text.toString()

        if (newMessagePage == 0) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val num = 1

                println("....................>>>$num")

                map["messageText"] = messageText
                map["userSend"] = UserDetail.user_id
                map["message_id"] = num.toString()

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)
                messageArea?.text = null
            }
        } else if (newMessagePage == 1) {
            if (messageText != "") {
                val map = HashMap<String, String>()

                val i = arrayMsgIDList?.get(arrayMsgIDList?.size!!.minus(1))
                val num = Integer.parseInt(i) + 1

                println("....................>>>$num")

                map["messageText"] = messageText
                map["userSend"] = UserDetail.user_id
                map["message_id"] = num.toString()

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)
                messageArea?.text = null
            }
        }

    }

    fun getDataMessage() {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History").child(UserDetail.user_id).child(selectedUser)
        arrayMsgIDList = ArrayList<String>()

        databaseRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                newMessagePage = 1
                val map = dataSnapshot.getValue(MessageData::class.java)
                arrayMsgIDList?.add(map?.messageText!!)
                println("----->>>> " + arrayMsgIDList?.size)

                addMessageBox(map?.messageText.toString(), map?.userSend.toString())
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

    fun addMessageBox(text: String, sender: String) {
        val textView = TextView(this@ChatRoom)
        textView.text = text
        textView.textSize = 20f
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.maxLines = 100


        val lp2 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp2.weight = 1.0f

        if (sender == UserDetail.user_id) {
            lp2.gravity = Gravity.RIGHT
            lp2.topMargin = 13
            textView.gravity = Gravity.CENTER or Gravity.LEFT
            textView.setBackgroundResource(R.drawable.speech_bubble2)

        } else {
            lp2.gravity = Gravity.LEFT
            lp2.topMargin = 18
            textView.gravity = Gravity.CENTER or Gravity.LEFT
            textView.setBackgroundResource(R.drawable.speech_bubble)

        }
        textView.layoutParams = lp2
        layout1?.addView(textView)
        scrollView?.fullScroll(View.FOCUS_DOWN)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, ChatHistory::class.java))
        finish()
    }
}

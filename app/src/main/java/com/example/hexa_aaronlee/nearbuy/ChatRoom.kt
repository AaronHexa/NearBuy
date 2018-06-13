package com.example.hexa_aaronlee.nearbuy

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.hexa_aaronlee.nearbuy.DatabaseData.MessageData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.media_dialog_box.view.*
import kotlinx.android.synthetic.main.message_area.*
import java.util.ArrayList
import java.util.HashMap
import android.widget.Toast
import android.app.ProgressDialog
import android.content.DialogInterface
import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.share_pic_box.*


class ChatRoom : AppCompatActivity(),ChatRoomView.view {

    private var selectedUser: String = ""
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRef2: DatabaseReference

    private var arrayMsgIDList: ArrayList<String>? = null
    private var newMessagePage: Int = 0

    lateinit var mStorage : FirebaseStorage
    var PICK_IMAGE_REQUEST =1234
    lateinit var filePath : Uri
    var imageFileName : String = " "
    private var uriTxt : String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        selectedUser = UserDetail.chatWithID
        newMessagePage = 0

        chatName.text = UserDetail.chatWithName

        val tmpUri = Uri.parse(UserDetail.chatWithImageUri)

        Picasso.get()
                .load(tmpUri)
                .centerCrop()
                .resize(700,700)
                .into(chatPic)

        System.out.println(selectedUser)

        getDataMessage()

        sendButton.setOnClickListener { saveMeassageData() }

        backFromChat.setOnClickListener {
            startActivity(Intent(applicationContext, ChatHistory::class.java))
            finish()
        }

        layout1.setOnClickListener { v -> hideKeyboard(v) }

        messageArea.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        setting_clip.setOnClickListener {
            showDialogBox()
        }
    }

    override fun setEditTextEmpty() {
        messageArea?.text = null
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
                map["msg_type"] = "Text"

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)

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
                map["msg_type"] = "Text"

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)
                messageArea?.text = null
            }
        }

    }

    fun saveImageData(uriTxt : String) {

        databaseRef = FirebaseDatabase.getInstance().reference.child("History")
        databaseRef2 = FirebaseDatabase.getInstance().reference.child("History")


        if (newMessagePage == 0) {

                val map = HashMap<String, String>()

                val num = 1

                println("....................>>>$num")

                map["messageText"] = uriTxt
                map["userSend"] = UserDetail.user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Picture"

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)
                messageArea?.text = null

        } else if (newMessagePage == 1) {

                val map = HashMap<String, String>()

                val i = arrayMsgIDList?.get(arrayMsgIDList?.size!!.minus(1))
                val num = Integer.parseInt(i) + 1

                println("....................>>>$num")

                map["messageText"] = uriTxt
                map["userSend"] = UserDetail.user_id
                map["message_id"] = num.toString()
                map["msg_type"] = "Picture"

                databaseRef.child(UserDetail.user_id).child(selectedUser).child(num.toString()).setValue(map)
                databaseRef2.child(UserDetail.chatWithID).child(UserDetail.user_id).child(num.toString()).setValue(map)
                messageArea?.text = null
        }
    }




    fun getDataMessage() {
        databaseRef = FirebaseDatabase.getInstance().reference.child("History").child(UserDetail.user_id).child(selectedUser)
        arrayMsgIDList = ArrayList<String>()

        databaseRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                newMessagePage = 1
                val map = dataSnapshot.getValue(MessageData::class.java)
                arrayMsgIDList?.add(map?.message_id!!)
                imageFileName = map?.message_id!!
                println("----->>>> " + arrayMsgIDList?.size)

                addMessageBox(map.messageText.toString(), map.userSend.toString(), map.msg_type.toString())
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

    fun addMessageBox(text: String, sender: String, type :String) {

        val lp2 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp2.weight = 1.0f

        if(type == "Text")
        {
            val textView = TextView(this@ChatRoom)
            textView.text = text
            textView.textSize = 20f
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.maxLines = 100

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
        }
        else if (type == "Picture")
        {
            val tmpUri = Uri.parse(text)
            val imageView = ImageView(this@ChatRoom)

            if (sender == UserDetail.user_id) {
                lp2.gravity = Gravity.RIGHT
                lp2.topMargin = 10
                imageView.setBackgroundResource(R.drawable.speech_bubble2)

            } else {
                lp2.gravity = Gravity.LEFT
                lp2.topMargin = 15
                imageView.setBackgroundResource(R.drawable.speech_bubble)
            }

            imageView.layoutParams = lp2
            layout1?.addView(imageView)
            System.out.println("...>>>>>>....$tmpUri")
            Picasso.get()
                    .load(tmpUri)
                    .resize(900, 900)
                    .centerCrop()
                    .into(imageView)
        }

        scrollView?.fullScroll(View.FOCUS_DOWN)
    }

    fun showDialogBox() {
        var builder = AlertDialog.Builder(this)
        var inflates = this.layoutInflater
        var customDialog = inflates.inflate(R.layout.media_dialog_box, null)
        builder.setView(customDialog)

        val dialog = builder.create()

        customDialog.media_document.setOnClickListener {

        }

        customDialog.media_music.setOnClickListener {
            chooseImageSent()
            dialog.dismiss()
        }

        customDialog.media_location.setOnClickListener {

        }

        dialog.show()
    }

    fun chooseImageSent(){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data

                confirmSharePic()
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun confirmSharePic()
    {
        var builder = AlertDialog.Builder(this)
        var inflates = this.layoutInflater
        var customDialog = inflates.inflate(R.layout.share_pic_box, null)
        builder.setView(customDialog)

        val tmpImageView : ImageView = customDialog.findViewById(R.id.sharePicConfirm)

        Picasso.get()
                .load(filePath)
                .resize(200, 200)
                .centerCrop()
                .into(tmpImageView)


        builder.setTitle("Confirmation")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->

            var tmpFileName : String = ""

            if (newMessagePage == 1)
            {
               tmpFileName = ((imageFileName.toInt()) + 1).toString()
            }
            else if (newMessagePage == 0)
            {
                tmpFileName = Integer.valueOf("1").toString()
            }


            mStorage = FirebaseStorage.getInstance()
            var mReference = mStorage.reference.child("PictureSent").child(UserDetail.user_id).child(UserDetail.chatWithID).child(tmpFileName)
            try {
                mReference.putFile(filePath).addOnSuccessListener {

                    Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()

                }.addOnFailureListener({ exception ->
                    //if the upload is not successfull
                    //hiding the progress dialog

                    //and displaying error message
                    Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
                })
                        .continueWithTask({ task ->
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }

                            // Continue with the task to get the download URL
                            mReference.downloadUrl
                        }).addOnCompleteListener({ task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result

                                println("....Download url....>>>" + downloadUri.toString())

                                saveImageData(downloadUri.toString())

                            } else {
                                // Handle failures
                                Toast.makeText(applicationContext, "File Fail To Upload  ", Toast.LENGTH_LONG).show()
                            }
                        })
            }catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }

            dialog.dismiss()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
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

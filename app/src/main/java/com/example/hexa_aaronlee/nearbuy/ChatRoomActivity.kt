package com.example.hexa_aaronlee.nearbuy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.media_dialog_box.view.*
import kotlinx.android.synthetic.main.message_area.*
import java.util.ArrayList
import android.widget.Toast
import android.content.DialogInterface
import com.example.hexa_aaronlee.nearbuy.Presenter.ChatRoomPresenter
import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.github.chrisbanes.photoview.PhotoView


class ChatRoomActivity : AppCompatActivity(), ChatRoomView.View {

    private var selectedUser: String = ""
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRef2: DatabaseReference

    private lateinit var arrayMsgIDList: ArrayList<String>
    private var newMessagePage: Int = 0

    lateinit var mStorage: FirebaseStorage
    var PICK_IMAGE_REQUEST = 1234
    lateinit var filePath: Uri
    var imageFileName: String = " "
    private var uriTxt: String = " "
    lateinit var mPresenter: ChatRoomPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        selectedUser = UserDetail.chatWithID
        newMessagePage = 0

        chatName.text = UserDetail.chatWithName

        val tmpUri = Uri.parse(UserDetail.chatWithImageUri)

        arrayMsgIDList = ArrayList()
        mPresenter = ChatRoomPresenter(this)

        Picasso.get()
                .load(tmpUri)
                .centerCrop()
                .resize(700, 700)
                .into(chatPic)

        System.out.println(selectedUser)

        arrayMsgIDList = ArrayList<String>()

        mPresenter.retrieveMsgData(UserDetail.user_id, selectedUser, arrayMsgIDList)

        sendButton.setOnClickListener {
            val messageText = messageArea.text.toString()
            mPresenter.saveChatMsg(messageText, UserDetail.user_id, arrayMsgIDList, newMessagePage, selectedUser)
        }

        backFromChat.setOnClickListener {
            startActivity(Intent(applicationContext, ChatHistoryActivity::class.java))
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


    override fun addMsgChat(newMessagePage: Int, imageFileName: String, text: String, sender: String, type: String, arrayMsgIDList: ArrayList<String>) {
        this.newMessagePage = newMessagePage
        this.imageFileName = imageFileName

        val lp2 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp2.weight = 1.0f

        mPresenter.createMsgBubble(text, sender, type, applicationContext, UserDetail.user_id, lp2, layout1)

        scrollView?.fullScroll(View.FOCUS_DOWN)
    }

    fun showDialogBox() {
        val builder = AlertDialog.Builder(this)
        val inflates = this.layoutInflater
        val customDialog = inflates.inflate(R.layout.media_dialog_box, null)
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

    fun chooseImageSent() {

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

    override fun viewLargeImage(tmpUri: Uri) {
        val mBuilder = android.support.v7.app.AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.dialog_image, null)
        val photoView = mView.findViewById<PhotoView>(R.id.photoView)
        Picasso.get()
                .load(tmpUri)
                .into(photoView)
        mBuilder.setView(mView)
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun confirmSharePic() {
        val builder = AlertDialog.Builder(this)
        val inflates = this.layoutInflater
        val customDialog = inflates.inflate(R.layout.share_pic_box, null)
        builder.setView(customDialog)

        val tmpImageView: ImageView = customDialog.findViewById(R.id.sharePicConfirm)

        Picasso.get()
                .load(filePath)
                .resize(200, 200)
                .centerCrop()
                .into(tmpImageView)


        builder.setTitle("Confirmation")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
            mPresenter.comfrimationPicSend(newMessagePage, UserDetail.user_id, selectedUser, filePath, imageFileName, dialog)
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    override fun saveImageData(uriTxt: String) {
        mPresenter.savePicMsg(uriTxt, UserDetail.user_id, arrayMsgIDList, newMessagePage, selectedUser)
    }

    override fun uploadImageFailed() {
        Toast.makeText(applicationContext, "File Fail To Upload  ", Toast.LENGTH_LONG).show()
    }

    override fun uploadImageSuccess() {
        Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
    }


    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, ChatHistoryActivity::class.java))
        finish()
    }
}

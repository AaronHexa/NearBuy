package com.example.hexa_aaronlee.nearbuy

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
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
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.Presenter.ChatRoomPresenter
import com.example.hexa_aaronlee.nearbuy.View.ChatRoomView
import com.github.chrisbanes.photoview.PhotoView


class ChatRoomActivity : AppCompatActivity(), ChatRoomView.View {

    private var selectedUser: String = ""

    private lateinit var arrayMsgIDList: ArrayList<String>
    private var newMessagePage: Int = 0

    var PICK_IMAGE_REQUEST = 1234
    lateinit var filePath: Uri
    var imageFileName: String = " "
    lateinit var mPresenter: ChatRoomPresenter
    lateinit var mProgressDialog : ProgressDialog

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

        Log.d("Selected User : " , UserDetail.saleSelectedId)

        arrayMsgIDList = ArrayList<String>()
        mProgressDialog = ProgressDialog(this)

        mPresenter.retrieveMsgData(UserDetail.user_id, selectedUser, arrayMsgIDList,UserDetail.saleSelectedId)

        sendButton.setOnClickListener {
            val messageText = messageArea.text.toString()
            mPresenter.saveChatMsg(messageText, UserDetail.user_id, arrayMsgIDList, newMessagePage, selectedUser,UserDetail.saleSelectedId)
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
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data

                confirmSharePic(filePath)
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

    fun confirmSharePic(filePath:Uri) {
        val builder = AlertDialog.Builder(this)
        val inflates = this.layoutInflater
        val customDialog = inflates.inflate(R.layout.share_pic_box, null)
        builder.setView(customDialog)

        val tmpImageView = customDialog.findViewById<ImageView>(R.id.sharePicConfirm)
        Log.i("FilePath : ", filePath.toString())

        Picasso.get()
                .load(filePath)
                .resize(200, 200)
                .centerCrop()
                .into(tmpImageView)


        builder.setTitle("Confirmation")
        builder.setPositiveButton("Yes", { dialog, whichButton ->
            mProgressDialog.setMessage("Uploading Please Wait...")
            mProgressDialog.show()

            mPresenter.comfrimationPicSend(newMessagePage, UserDetail.user_id, selectedUser, filePath, imageFileName, dialog,UserDetail.saleSelectedId)
        })

        builder.setNegativeButton("Cancel", { dialog, whichButton ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    override fun saveImageData(uriTxt: String) {

        mPresenter.savePicMsg(uriTxt, UserDetail.user_id, arrayMsgIDList, newMessagePage, selectedUser,UserDetail.saleSelectedId)
    }

    override fun uploadImageFailed() {
        Toast.makeText(applicationContext, "File Fail To Upload  ", Toast.LENGTH_LONG).show()
        mProgressDialog.dismiss()
    }

    override fun uploadImageSuccess() {
        Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
        mProgressDialog.dismiss()
    }


    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        //startActivity(Intent(applicationContext, ChatHistoryActivity::class.java))
        finish()
    }
}

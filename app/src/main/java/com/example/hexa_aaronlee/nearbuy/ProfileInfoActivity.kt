package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_info.*
import kotlinx.android.synthetic.main.editing_dialog.view.*
import com.example.hexa_aaronlee.nearbuy.Presenter.ProfileInfoPresenter
import com.example.hexa_aaronlee.nearbuy.View.ProfileInfoView


class ProfileInfoActivity : AppCompatActivity(), ProfileInfoView.View {


    var filePath: Uri = Uri.EMPTY

    var profileUri: Uri = Uri.EMPTY
    var nameAcc: String = ""
    var PICK_IMAGE_REQUEST = 1
    var selectedImage: Int = 0

    lateinit var view: View
    lateinit var mPresenter: ProfileInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        nameAcc = UserDetail.username

        mPresenter = ProfileInfoPresenter(this)

        mPresenter.getProfileData(UserDetail.user_id)

        editProfile.setOnClickListener {
            showDialog()
        }

        linkToMySale.setOnClickListener {
            startActivity(Intent(applicationContext, MySaleList::class.java))
            //finish()
        }
    }

    override fun UpdateUI(profileImageUrl: String) {
        profileName.text = UserDetail.username
        profileEmail.text = "Email : " + UserDetail.email

        UserDetail.imageUrl = profileImageUrl

        profileUri = Uri.parse(profileImageUrl)

        Picasso.get()
                .load(profileUri)
                .resize(700, 700)
                .centerCrop()
                .into(profileImage)

    }

    fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        view = inflater.inflate(R.layout.editing_dialog, null)

        view.nameEdit.setText(UserDetail.username)

        Picasso.get()
                .load(profileUri)
                .resize(200, 200)
                .centerCrop()
                .into(view.imageEdited)

        filePath = profileUri

        view.imageEdited.setOnClickListener {
            selectedImage = 1
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }


        builder.setView(view)
        builder.setTitle("Edit Profile")
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }

        builder.setPositiveButton("Edit") { dialog, _ ->
            dialog?.dismiss()

            mPresenter.saveProfilePic(filePath, UserDetail.user_id, UserDetail.imageUrl, selectedImage)
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    override fun uploadImageError(exception: Exception) {
        Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
    }

    override fun uploadImageFailed() {

    }

    override fun uploadImageSuccess(uriTxt: String) {
        Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show()

        val tmpName = view.nameEdit.text.toString().trim()

        mPresenter.saveInDatabse(uriTxt, tmpName, UserDetail.user_id)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val edit: ImageView = view.findViewById(R.id.imageEdited)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data

                Picasso.get()
                        .load(filePath)
                        .resize(700, 700)
                        .centerCrop()
                        .into(edit)
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        //startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
    }
}

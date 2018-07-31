package com.example.hexa_aaronlee.nearbuy.Activity

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_info.*
import kotlinx.android.synthetic.main.editing_dialog.view.*
import com.example.hexa_aaronlee.nearbuy.Presenter.ProfileInfoPresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.ProfileInfoView
import java.util.regex.Pattern


class ProfileInfoActivity : AppCompatActivity(), ProfileInfoView.View {


    var filePath: Uri = Uri.EMPTY

    var profileUri: Uri = Uri.EMPTY
    var nameAcc: String = ""
    var PICK_IMAGE_REQUEST = 1
    var selectedImage: Int = 0
    var tmpPhoneNum : String = ""
    var genderEdition : String = ""

    lateinit var view: View
    lateinit var mPresenter: ProfileInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        nameAcc = UserDetail.username

        mPresenter = ProfileInfoPresenter(this)

        val actionBar = this.supportActionBar!!

        actionBar.title = "Profile Info"
        actionBar.setDisplayHomeAsUpEnabled(true)

        mPresenter.getProfileData(UserDetail.user_id)

        editProfile.setOnClickListener {
            showDialog()
        }

        linkToMySale.setOnClickListener {
            startActivity(Intent(applicationContext, MySaleList::class.java))
            //finish()
        }
    }

    override fun UpdateUI(profileImageUrl: String, userPhoneNum : String, userGender : String) {
        profileName.text = UserDetail.username
        profileEmail.text = Editable.Factory.getInstance().newEditable( UserDetail.email)
        profilePhoneNum.text = Editable.Factory.getInstance().newEditable(userPhoneNum)
        profileGender.text = userGender

        UserDetail.imageUrl = profileImageUrl
        UserDetail.dialog_phoneNum = userPhoneNum

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

        var checkSelecteGender = 0

        val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!_-]")

        view.nameEdit.setText(UserDetail.username)
        view.phoneNumEdit.setText(UserDetail.dialog_phoneNum)


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

        view.maleGenderEdit.setOnClickListener {

            checkSelecteGender = 1

            val imgIcon = view.findViewById<TextView>(R.id.maleGenderEdit)
            val backgroundGradient = imgIcon.background as GradientDrawable
            backgroundGradient.setColor(resources.getColor(R.color.colorLightBlue))

            val imgIcon2 = view.findViewById<TextView>(R.id.femaleGenderEdit)
            val backgroundGradient2 = imgIcon2.background as GradientDrawable
            backgroundGradient2.setColor(resources.getColor(R.color.colorLightGrey))
            genderEdition = "Male"
        }

        view.femaleGenderEdit.setOnClickListener {
            checkSelecteGender = 1

            val imgIcon = view.findViewById<TextView>(R.id.femaleGenderEdit)
            val backgroundGradient = imgIcon.background as GradientDrawable
            backgroundGradient.setColor(resources.getColor(R.color.colorLightBlue))

            val imgIcon2 = view.findViewById<TextView>(R.id.maleGenderEdit)
            val backgroundGradient2 = imgIcon2.background as GradientDrawable
            backgroundGradient2.setColor(resources.getColor(R.color.colorLightGrey))
            genderEdition = "Female"
        }

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }

        builder.setPositiveButton("Edit") { dialog, _ ->

            if(view.nameEdit == null || view.phoneNumEdit == null || checkSelecteGender == 0)
            {
                if(regex.matcher(view.nameEdit.text).find()){
                    Toast.makeText(this, "No Special Character", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Editing Failed",Toast.LENGTH_SHORT).show()
                }

            }else{
                dialog?.dismiss()
                mPresenter.saveProfilePic(filePath, UserDetail.user_id, UserDetail.imageUrl, selectedImage)
            }
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
        tmpPhoneNum = view.phoneNumEdit.text.toString().trim()

        UserDetail.username = tmpName
        UserDetail.dialog_phoneNum = tmpPhoneNum
        UserDetail.imageUrl = uriTxt

        mPresenter.saveInDatabse(uriTxt, tmpName, UserDetail.user_id,tmpPhoneNum,genderEdition)
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

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
        startActivity(Intent(applicationContext,MainPageActivity::class.java))
    }
}

package com.example.hexa_aaronlee.nearbuy

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_info.*
import kotlinx.android.synthetic.main.editing_dialog.view.*

class ProfileInfo : AppCompatActivity() {

    lateinit var mStorage : FirebaseStorage
    lateinit var filePath : Uri
    lateinit var databaseR : DatabaseReference
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatafaceReference: DatabaseReference

    var profileUri: Uri? = null
    var profileImageUrl : String = ""
    var nameAcc : String = ""
    var PICK_IMAGE_REQUEST = 1
    var selectedImage : Int = 0

    lateinit var view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        nameAcc = UserDetail.username

        getProfileData()

        editProfile.setOnClickListener{
            showDialog()
        }

    }

    fun getProfileData()
    {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(UserDetail.user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)
                profileImageUrl = data?.profilePhoto.toString()
                UserDetail.imageUrl = profileImageUrl
                profileUri = Uri.parse(profileImageUrl)

                UpdateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun UpdateUI()
    {
        profileName.text = UserDetail.username
        profileEmail.text = "Email : " + UserDetail.email

        Picasso.get()
                .load(profileUri)
                .resize(200, 200)
                .centerCrop()
                .into(profileImage)

    }

    fun showDialog()
    {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater : LayoutInflater = layoutInflater
        view = inflater.inflate(R.layout.editing_dialog,null)

        view.nameEdit.setText(UserDetail.username)

        Picasso.get()
                .load(profileUri)
                .resize(200, 200)
                .centerCrop()
                .into(view.imageEdited)

        view.imageEdited.setOnClickListener{
            selectedImage = 1
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }


        builder.setView(view)
        builder.setTitle("Edit Profile")
        builder.setNegativeButton("Cancel",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }

        })

        builder.setPositiveButton("Edit",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                dialog?.dismiss()

                var tmpName = view.nameEdit.text.toString().trim()

                mStorage = FirebaseStorage.getInstance()
                var mReference = mStorage.reference.child("ProfilePicture")
                try {
                    if(selectedImage == 0)
                    {
                        filePath = Uri.parse(UserDetail.imageUrl)
                    }

                    mReference.child(UserDetail.user_id).putFile(filePath).addOnSuccessListener {
                        taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                        val uriTxt = url.toString()
                        System.out.println(".......$tmpName............$url")
                        Toast.makeText(view.context, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()

                        saveDatabase(uriTxt,tmpName)
                    }
                }catch (e: Exception) {
                    Toast.makeText(view.context, e.toString(), Toast.LENGTH_LONG).show()
                }

            }

        })

        val dialog: Dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val edit : ImageView = view.findViewById(R.id.imageEdited)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data

                Picasso.get()
                        .load(filePath)
                        .resize(200, 200)
                        .centerCrop()
                        .into(edit)
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun saveDatabase(uriTxt: String, tmpName: String)
    {
        mDatafaceReference = FirebaseDatabase.getInstance().reference.child("User")


        mDatafaceReference.child(UserDetail.user_id).child("profilePhoto").setValue(uriTxt)

        mDatafaceReference.child(UserDetail.user_id).child("name").setValue(tmpName)
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainPage::class.java))
        finish()
    }
}

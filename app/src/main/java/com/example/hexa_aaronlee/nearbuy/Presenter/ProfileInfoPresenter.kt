package com.example.hexa_aaronlee.nearbuy.Presenter

import android.net.Uri
import android.util.Log
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.ProfileInfoView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ProfileInfoPresenter(internal var view : ProfileInfoView.View) : ProfileInfoView.Presenter {

    lateinit var databaseR : DatabaseReference
    lateinit var mStorage : FirebaseStorage
    var filePath : Uri = Uri.EMPTY

    override fun getProfileData(user_id: String) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)!!
                val profileImageUrl = data.profilePhoto
                val userPhoneNum = data.phoneNum

                view.UpdateUI(profileImageUrl,userPhoneNum,data.gender)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    override fun saveProfilePic(tmpfilePath: Uri, user_id: String, oldImageUri: String, selectedImage: Int) {

        mStorage = FirebaseStorage.getInstance()
        val mReference = mStorage.reference.child("ProfilePicture").child(user_id)


        try {
            if(selectedImage == 0) {
                filePath = Uri.parse(oldImageUri)
                view.uploadImageSuccess(oldImageUri)
            }
            else {
                filePath = tmpfilePath

                mReference.putFile(filePath)
                        .addOnFailureListener({ exception ->
                            //if the upload is not successfull
                            //hiding the progress dialog

                            //and displaying error message
                            view.uploadImageError(exception)

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

                                Log.i("uri :" , downloadUri.toString())

                                view.uploadImageSuccess(downloadUri.toString())


                            } else {
                                // Handle failures
                                view.uploadImageFailed()

                            }
                        })
            }
        }catch (e: Exception) {
            //Toast.makeText(view.context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun saveInDatabse(uriTxt: String, tmpName: String,user_id: String,tmpPhoneNum:String, tmpGender : String) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User")


        databaseR.child(user_id).child("profilePhoto").setValue(uriTxt)

        databaseR.child(user_id).child("name").setValue(tmpName)

        databaseR.child(user_id).child("phoneNum").setValue(tmpPhoneNum)

        databaseR.child(user_id).child("gender").setValue(tmpGender)
    }

}
package com.example.hexa_aaronlee.nearbuy.Presenter

import android.net.Uri
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.RegisterView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask


public class RegisterPresenter(internal var view: RegisterView.view):RegisterView.presenter {

    lateinit var mStorage : FirebaseStorage
    lateinit var mDatafaceReference: DatabaseReference

    override fun saveUserDataToDatabase(email:String,password:String,name:String,id:String,uriTxt:String) {
        mDatafaceReference = FirebaseDatabase.getInstance().reference.child("User")


        val data = UserData(email,password,name,id,uriTxt,"Email")

        mDatafaceReference.child(id).setValue(data)
    }

    override fun saveProfilePicToStorage(tmpID : String,filePath : Uri) {
        mStorage = FirebaseStorage.getInstance()
        var mReference = mStorage.reference.child("ProfilePicture").child(tmpID)
        try {
            mReference.putFile(filePath).addOnSuccessListener {
                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.uploadSessionUri

            }
                    .continueWithTask({ task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }

                        // Continue with the task to get the download URL
                        mReference.downloadUrl
                    }).addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result

                            var uriTxt = downloadUri.toString()

                            view.toastUploadSuccess(uriTxt)
                            println("....Download url....>>>" + downloadUri.toString())

                        }
                    })
        }catch (e: Exception) {
            view.toastUploadFailed(e)
        }
    }
}
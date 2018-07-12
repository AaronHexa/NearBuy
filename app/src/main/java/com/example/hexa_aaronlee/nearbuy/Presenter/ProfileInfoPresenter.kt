package com.example.hexa_aaronlee.nearbuy.Presenter

import android.net.Uri
import android.util.Log
import android.widget.ExpandableListView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.View.ProfileInfoView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ProfileInfoPresenter(internal var view: ProfileInfoView.View) : ProfileInfoView.Presenter {

    lateinit var databaseR: DatabaseReference
    lateinit var databaseR2: DatabaseReference
    lateinit var databaseR3: DatabaseReference
    lateinit var databaseR4: DatabaseReference
    lateinit var mStorage: FirebaseStorage
    var filePath: Uri = Uri.EMPTY

    override fun getProfileData(user_id: String) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)!!
                val profileImageUrl = data.profilePhoto
                val userPhoneNum = data.phoneNum

                view.UpdateUI(profileImageUrl, userPhoneNum, data.gender)
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
            if (selectedImage == 0) {
                filePath = Uri.parse(oldImageUri)
                view.uploadImageSuccess(oldImageUri)
            } else {
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

                                Log.i("uri :", downloadUri.toString())

                                view.uploadImageSuccess(downloadUri.toString())


                            } else {
                                // Handle failures
                                view.uploadImageFailed()

                            }
                        })
            }
        } catch (e: Exception) {
            //Toast.makeText(view.context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun saveInDatabse(uriTxt: String, reName: String, user_id: String, tmpPhoneNum: String, tmpGender: String) {
        databaseR = FirebaseDatabase.getInstance().reference.child("User")


        databaseR.child(user_id).child("profilePhoto").setValue(uriTxt)

        databaseR.child(user_id).child("name").setValue(reName)

        databaseR.child(user_id).child("phoneNum").setValue(tmpPhoneNum)

        databaseR.child(user_id).child("gender").setValue(tmpGender)

        saveInSaleDetail(reName, user_id)
        saveInTotalHistory(reName,user_id)
    }

    fun saveInSaleDetail(reName: String, user_id: String) {

        val saleIdList = ArrayList<String>()
        var tmpNum = 0

        Log.i("Sale id :", " Start")
        databaseR2 = FirebaseDatabase.getInstance().reference.child("SaleDetail").child(user_id)

        databaseR2.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(DealsDetailData::class.java)!!

                    saleIdList.add(data.sales_id)

                    Log.i("Sale id :", " ${data.sales_id}")

                    tmpNum += 1
                    val tmpString = tmpNum.toString()

                    if (tmpString == dataSnapshot.childrenCount.toString()) {
                        RenameInSaleDetail(saleIdList,reName,user_id)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("Database Error : ", p0.message)
            }

        })
    }

    fun RenameInSaleDetail(saleIdList: ArrayList<String>, reName: String, user_id: String){
        databaseR2 = FirebaseDatabase.getInstance().reference.child("SaleDetail")

        for (i in saleIdList.indices){
            databaseR2.child(user_id).child(saleIdList[i]).child("offerBy").setValue(reName)
        }
    }

    fun saveInTotalHistory(reName: String, user_id: String){
        val chatListKeyArray = ArrayList<String>()
        val historyUserList = ArrayList<String>()
        var tmpNum = 0

        Log.i("TotalHistory :", " Start")
        databaseR3 = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id)

        databaseR3.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)!!

                    chatListKeyArray.add(data.chatListKey)
                    historyUserList.add(data.history_user)

                    Log.i("Sale id :", " ${data.sale_id}")

                    tmpNum += 1
                    val tmpString = tmpNum.toString()

                    if (tmpString == dataSnapshot.childrenCount.toString()) {
                        RenameInTotalHistory(chatListKeyArray,historyUserList,reName)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("Database Error : ", p0.message)
            }

        })
    }

    fun RenameInTotalHistory(chatListKeyArray: ArrayList<String>,historyUserList: ArrayList<String>, reName: String){
        databaseR3 = FirebaseDatabase.getInstance().reference.child("TotalHistory")

        for (i in chatListKeyArray.indices){
            databaseR3.child(historyUserList[i]).child(chatListKeyArray[i]).child("history_userName").setValue(reName)
        }
    }
}
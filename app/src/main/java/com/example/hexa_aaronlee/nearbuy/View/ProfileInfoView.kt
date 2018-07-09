package com.example.hexa_aaronlee.nearbuy.View

import android.net.Uri

interface ProfileInfoView {
    interface View {
        fun UpdateUI(profileImageUrl: String,
                     userPhoneNum : String,
                     userGender : String)
        fun uploadImageSuccess(uriTxt: String)
        fun uploadImageFailed()
        fun uploadImageError(exception: Exception)
    }

    interface Presenter {
        fun getProfileData(user_id: String)
        fun saveProfilePic(tmpfilePath: Uri,
                           user_id: String,
                           oldImageUri: String,
                           selectedImage: Int)

        fun saveInDatabse(uriTxt: String,
                          tmpName: String,
                          user_id: String,
                          tmpPhoneNum : String,
                          tmpGender : String)
    }
}
package com.example.hexa_aaronlee.nearbuy.View

import android.net.Uri

public interface RegisterView {
    interface View {
        fun toastUploadSuccess(uriTxt: String)
        fun toastUploadFailed(e: Exception)
    }

    interface Presenter {
        fun saveUserDataToDatabase(email: String,
                                   password: String,
                                   name: String,
                                   id: String,
                                   uriTxt: String)

        fun saveProfilePicToStorage(tmpID: String,
                                    filePath: Uri)
    }
}
package com.example.hexa_aaronlee.nearbuy.View

interface ViewSaleDetailView {

    interface View {
        fun updateUI(salesImage: String,
                     itemTitle: String,
                     itemPrice: String,
                     itemDescription: String,
                     itemLocation: String,
                     mLatitude: String,
                     mLongitude: String,
                     offerBy: String,
                     offer_id: String)

        fun updateInfo(profilePhoto: String,
                       name: String,
                       chatUserId: String)

        fun saveHistoryData(checkedResult: Boolean,chatListKey:String)

        fun SuccessfulSaveData(chatListKey: String)
    }

    interface Presenter {
        fun getSalesDetail(saleSelectedId: String,
                           saleOfferId: String)

        fun getChatDetail(checkDealer: String)
        fun saveUserToHistoryChat(profilePhoto: String,
                                  user_id: String,
                                  checkDealer: String,
                                  username: String,
                                  tmpSaleTitle: String,
                                  saleSelectedId: String,
                                  dealerName: String,
                                  dealerPic: String)

        fun checkHistorySaleData(saleSelectedId: String,
                                 user_id: String)

    }
}
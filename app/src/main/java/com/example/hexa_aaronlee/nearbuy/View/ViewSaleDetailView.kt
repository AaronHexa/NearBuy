package com.example.hexa_aaronlee.nearbuy.View

interface ViewSaleDetailView{

    interface View{
        fun updateUI(salesImage:String,itemTitle:String,itemPrice:String,itemDescription:String,itemLocation:String,mLatitude:String,mLongitude:String,offerBy:String,offer_id:String)
        fun updateInfo(profilePhoto : String, name : String)
    }

    interface Presenter{
        fun getSalesDetail(saleSelectedId : String)
        fun getChatDetail(checkDealer : String)
        fun saveUserToHistoryChat(profilePhoto: String,user_id : String, checkDealer: String, tmpSaleUser : String , tmpSaleTitle :String)
    }
}
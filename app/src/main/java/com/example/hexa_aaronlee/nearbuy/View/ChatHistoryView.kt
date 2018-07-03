package com.example.hexa_aaronlee.nearbuy.View


interface ChatHistoryView {

    interface View {
        fun setEmptyViewAdapter(checkDataExits: Boolean)
        fun setRecyclerViewAdapter(historyData: ArrayList<String>,
                                   imageData: ArrayList<String>,
                                   nameData: ArrayList<String>,
                                   titleData: ArrayList<String>,
                                   saleData: ArrayList<String>,
                                   chatDate: ArrayList<String>,
                                   chatTime: ArrayList<String>)
    }

    interface Presenter {
        fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>,
                                           imageData: ArrayList<String>,
                                           nameData: ArrayList<String>,
                                           titleData: ArrayList<String>,
                                           user_id: String,
                                           saleData: ArrayList<String>,
                                           chatDate: ArrayList<String>,
                                           chatTime: ArrayList<String>)
        fun checkChatHistiryData(user_id: String)
    }
}
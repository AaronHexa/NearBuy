package com.example.hexa_aaronlee.nearbuy.View


interface ChatHistoryView {

    interface View {
        fun setEmptyViewAdapter(checkDataExits: Boolean)
        fun setRecyclerViewAdapter(historyData: ArrayList<String>,
                                   imageData: ArrayList<String>,
                                   nameData: ArrayList<String>,
                                   titleData: ArrayList<String>,
                                   saleData: ArrayList<String>,
                                   msg_status: ArrayList<String>,
                                   msg_statusCount: ArrayList<Int>)
    }

    interface Presenter {
        fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>,
                                           imageData: ArrayList<String>,
                                           nameData: ArrayList<String>,
                                           titleData: ArrayList<String>,
                                           user_id: String,
                                           saleData: ArrayList<String>,
                                           msg_status: ArrayList<String>,
                                           msg_statusCount: ArrayList<Int>)
        fun checkChatHistiryData(user_id: String)
    }
}
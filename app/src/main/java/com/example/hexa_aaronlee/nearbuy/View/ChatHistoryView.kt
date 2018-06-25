package com.example.hexa_aaronlee.nearbuy.View


interface ChatHistoryView {

    interface View {
        fun setRecyclerViewAdapter(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>)
    }

    interface Presenter {
        fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>, imageData: ArrayList<String>, nameData: ArrayList<String>, titleData: ArrayList<String>, user_id: String)
    }
}
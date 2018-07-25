package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData


interface ChatHistoryView {

    interface View {
        fun setEmptyViewAdapter(checkDataExits: Boolean)
        fun setRecyclerViewAdapter(dataList: ArrayList<HistoryData>)
    }

    interface Presenter {

        fun checkChatHistiryData(user_id: String)

        fun getHistory(user_id: String)
    }
}
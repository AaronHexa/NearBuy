package com.example.hexa_aaronlee.nearbuy.View


interface ChatHistoryView{

    interface view{
        fun setRecyclerViewAdapter(historyData: ArrayList<String>,imageData: ArrayList<String>,nameData: ArrayList<String>,titleData: ArrayList<String>)
    }

    interface presenter{
        fun getChatHistoryDataFromDatabase(historyData: ArrayList<String>,imageData: ArrayList<String>,nameData: ArrayList<String>,titleData: ArrayList<String>,user_id:String)
    }
}
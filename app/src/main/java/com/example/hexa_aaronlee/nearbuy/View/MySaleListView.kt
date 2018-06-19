package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail

interface MySaleListView
{
    interface View
    {
        fun updateList(lstDetail : ArrayList<DealsDetail>)
        fun setDeleteBtn(lstDetail: ArrayList<DealsDetail>)
    }

    interface Presenter{
        fun getSaledata(user_id : String,lstDetail : ArrayList<DealsDetail>)
        fun deleteSaleInDatabase (mDeletionPos : ArrayList<Int>,lstDetail: ArrayList<DealsDetail>,user_id: String)
    }
}
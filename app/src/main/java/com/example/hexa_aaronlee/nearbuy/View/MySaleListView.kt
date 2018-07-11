package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData

interface MySaleListView {
    interface View {
        fun updateList(lstDetail: ArrayList<DealsDetailData>)
        fun setDeleteBtn(lstDetail: ArrayList<DealsDetailData>)
        fun FinishDeletion()
    }

    interface Presenter {
        fun checkSaleData(user_id: String,
                          lstDetail: ArrayList<DealsDetailData>)

        fun deleteSaleInDatabase(mDeletionPos: ArrayList<Int>,
                                 lstDetail: ArrayList<DealsDetailData>,
                                 user_id: String)
    }
}
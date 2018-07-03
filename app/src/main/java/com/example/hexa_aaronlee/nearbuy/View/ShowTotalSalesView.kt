package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData

interface ShowTotalSalesView {

    interface View {
        fun updateList(lstSaleData: ArrayList<DealsDetailData>)
        fun setLoopCheckSale(lstUserId: ArrayList<String>)
    }

    interface Presenter {
        fun getAllUserID(lstUserId: ArrayList<String>)
        fun getSaleData(lstSaleData: ArrayList<DealsDetailData>,
                        user_id: String)

        fun getSaleDataWithLimitDistance(lstSaleData: ArrayList<DealsDetailData>,
                                         mLatitude: Double,
                                         mLongitude: Double,
                                         user_id: String)
    }

}
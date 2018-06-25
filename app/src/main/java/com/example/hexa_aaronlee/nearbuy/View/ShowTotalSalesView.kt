package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData

interface ShowTotalSalesView {

    interface View {
        fun updateList(lstSaleData: ArrayList<DealsDetailData>)
    }

    interface Presenter {
        fun getSaleData(lstSaleData: ArrayList<DealsDetailData>)
    }

}
package com.example.hexa_aaronlee.nearbuy.View

import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail

interface ShowTotalSalesView{

    interface View{
        fun updateList(lstSaleData : ArrayList<DealsDetail>)
    }

    interface Presenter{
        fun getSaleData(lstSaleData : ArrayList<DealsDetail>)
    }

}
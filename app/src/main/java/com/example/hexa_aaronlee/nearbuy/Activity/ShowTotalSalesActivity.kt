package com.example.hexa_aaronlee.nearbuy.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.hexa_aaronlee.nearbuy.Presenter.ShowTotalSalesPresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.Adapter.RecyclerViewAdapter
import com.example.hexa_aaronlee.nearbuy.View.ShowTotalSalesView
import kotlinx.android.synthetic.main.activity_show_total_sales.*


class ShowTotalSalesActivity : AppCompatActivity(), ShowTotalSalesView.View {

    lateinit var mPresenter: ShowTotalSalesPresenter

    lateinit var lstSaleData: ArrayList<DealsDetailData>
    lateinit var lstUserId: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_total_sales)

        lstSaleData = ArrayList()
        lstUserId = ArrayList()

        mPresenter = ShowTotalSalesPresenter(this)

        mPresenter.getAllUserID(lstUserId)
    }

    override fun setLoopCheckSale(lstUserId: ArrayList<String>) {
        for (i in 0 until lstUserId.count()-1){
            mPresenter.getSaleData(lstSaleData,lstUserId[i])
        }
    }

    override fun updateList(lstSaleData: ArrayList<DealsDetailData>) {
        val myrv = findViewById<RecyclerView>(R.id.listSalesView)
        val myAdapter = RecyclerViewAdapter(this, lstSaleData,UserDetail.mLatitude,UserDetail.mLongitude,0)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }

    override fun onBackPressed() {
        finish()
    }
}

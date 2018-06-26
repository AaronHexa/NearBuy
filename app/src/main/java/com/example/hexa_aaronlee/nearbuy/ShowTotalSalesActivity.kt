package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.hexa_aaronlee.nearbuy.Presenter.ShowTotalSalesPresenter
import com.example.hexa_aaronlee.nearbuy.View.ShowTotalSalesView
import kotlinx.android.synthetic.main.activity_show_total_sales.*


class ShowTotalSalesActivity : AppCompatActivity(), ShowTotalSalesView.View {

    lateinit var mPresenter: ShowTotalSalesPresenter

    lateinit var lstSaleData: ArrayList<DealsDetailData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_total_sales)

        lstSaleData = ArrayList()

        mPresenter = ShowTotalSalesPresenter(this)

        mPresenter.getSaleData(lstSaleData)

        checkBoxLayout.setOnClickListener {
            if (!checkBoxLimitation.isChecked)
            {
                lstSaleData = ArrayList()
                checkBoxLimitation.isChecked = true
                mPresenter.getSaleDataWithLimitDistance(lstSaleData,UserDetail.mLatitude,UserDetail.mLongitude)
            }
            else if (checkBoxLimitation.isChecked)
            {
                lstSaleData = ArrayList()
                checkBoxLimitation.isChecked = false
                mPresenter.getSaleData(lstSaleData)
            }


        }
    }

    override fun updateList(lstSaleData: ArrayList<DealsDetailData>) {
        val myrv = findViewById<RecyclerView>(R.id.listSalesView)
        val myAdapter = RecyclerViewAdapter(this, lstSaleData)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
    }
}

package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.example.hexa_aaronlee.nearbuy.Presenter.MySaleListPresenter
import com.example.hexa_aaronlee.nearbuy.View.MySaleListView
import com.google.firebase.database.*

class MySaleList : AppCompatActivity(), MySaleListView.View {

    lateinit var mDataRef : DatabaseReference

    lateinit var lstDetail : ArrayList<DealsDetail>

    lateinit var mPresenter :MySaleListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sale_list)

        lstDetail = ArrayList()
        mPresenter = MySaleListPresenter(this)

        mPresenter.getSaledata(UserDetail.user_id,lstDetail)
    }

    override fun updateList(lstDetail: ArrayList<DealsDetail>) {
        val myrv = findViewById<RecyclerView>(R.id.listMySale)
        val myAdapter = RecyclerViewAdapter("700",this, lstDetail)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, ProfileInfo::class.java))
        finish()
    }
}

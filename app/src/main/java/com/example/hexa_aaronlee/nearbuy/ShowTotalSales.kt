package com.example.hexa_aaronlee.nearbuy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView





class ShowTotalSales : AppCompatActivity() {

    lateinit var lstBook : ArrayList<DealsDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_total_sales)

        lstBook = ArrayList()
        lstBook.add(DealsDetail("The Vegitarian", "12", "Description book","Kuala Lumpur","30.00","50.00","Wong","xxxzzz","https://firebasestorage.googleapis.com/v0/b/nearbuy-f6843.appspot.com/o/SalesImage%2FNd5yMoqu8cfyLxwB4V9QfRV78wC2%2F-LESxusvBTjfSCeubCJi%2Fimage0?alt=media&token=6af9fa2d-fda9-469f-9575-a6c918b7a268" ))
        lstBook.add(DealsDetail("The Wild Robot", "35", "Description book", "Kuala Lumpur","30.00","50.00","Wong","xxxzzz","https://firebasestorage.googleapis.com/v0/b/nearbuy-f6843.appspot.com/o/SalesImage%2FNd5yMoqu8cfyLxwB4V9QfRV78wC2%2F-LESxusvBTjfSCeubCJi%2Fimage0?alt=media&token=6af9fa2d-fda9-469f-9575-a6c918b7a268"))
        lstBook.add(DealsDetail("The Vegitarian", "12", "Description book","Kuala Lumpur","30.00","50.00","Wong","xxxzzz","https://firebasestorage.googleapis.com/v0/b/nearbuy-f6843.appspot.com/o/SalesImage%2FNd5yMoqu8cfyLxwB4V9QfRV78wC2%2F-LESxusvBTjfSCeubCJi%2Fimage0?alt=media&token=6af9fa2d-fda9-469f-9575-a6c918b7a268" ))
        lstBook.add(DealsDetail("The Wild Robot", "35", "Description book", "Kuala Lumpur","30.00","50.00","Wong","xxxzzz","https://firebasestorage.googleapis.com/v0/b/nearbuy-f6843.appspot.com/o/SalesImage%2FNd5yMoqu8cfyLxwB4V9QfRV78wC2%2F-LESxusvBTjfSCeubCJi%2Fimage0?alt=media&token=6af9fa2d-fda9-469f-9575-a6c918b7a268"))

        val myrv = findViewById(R.id.listSalesView) as RecyclerView
        val myAdapter = RecyclerViewAdapter("700",this, lstBook)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }
}

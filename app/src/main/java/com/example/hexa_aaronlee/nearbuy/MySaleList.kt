package com.example.hexa_aaronlee.nearbuy

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import com.example.hexa_aaronlee.nearbuy.Presenter.MySaleListPresenter
import com.example.hexa_aaronlee.nearbuy.View.MySaleListView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_sale_list.*

class MySaleList : AppCompatActivity(), MySaleListView.View {

    lateinit var lstDetail : ArrayList<DealsDetail>

    lateinit var mPresenter :MySaleListPresenter

    lateinit var mDeletionPos : ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sale_list)

        lstDetail = ArrayList()
        mPresenter = MySaleListPresenter(this)

        mPresenter.getSaledata(UserDetail.user_id,lstDetail)
    }

    override fun updateList(lstDetail: ArrayList<DealsDetail>) {
        val myrv = findViewById<RecyclerView>(R.id.listMySale)
        val myAdapter = RecyclerViewAdapter(this, lstDetail)
        myrv.layoutManager = GridLayoutManager(this, 2)
        myrv.adapter = myAdapter
    }

    override fun setDeleteBtn(lstDetail: ArrayList<DealsDetail>) {
        deleteLayoutBtn.setOnClickListener {
            val mBuilder = android.support.v7.app.AlertDialog.Builder(this)
            val mView = layoutInflater.inflate(R.layout.delete_my_sale, null)
            val cancelBtn = mView.findViewById<Button>(R.id.cancelDeleteSaleBtn)
            val deleteBtn = mView.findViewById<Button>(R.id.deleteSaleBtn)
            val recyclerV = mView.findViewById<RecyclerView>(R.id.deleteRecyclerView)

            mDeletionPos = ArrayList()

            val myAdapter = DeletionRecyclerViewAdapter(this, lstDetail,mDeletionPos)
            val layoutManager = LinearLayoutManager(this)
            recyclerV.layoutManager = layoutManager
            recyclerV.adapter = myAdapter

            mBuilder.setView(mView)
            val mDialog = mBuilder.create()

            cancelBtn.setOnClickListener{ mDialog.dismiss() }

            deleteBtn.setOnClickListener {
                mPresenter.deleteSaleInDatabase(mDeletionPos,lstDetail,UserDetail.user_id)
                Toast.makeText(this,"Delete Success",Toast.LENGTH_SHORT).show()
                mDialog.dismiss()
            }

            mDialog.show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, ProfileInfo::class.java))
        finish()
    }

}

class DeletionRecyclerViewAdapter(private val mContext : Context ,private val mData: List<DealsDetail>,private var mDeletionPos: ArrayList<Int>) : RecyclerView.Adapter<DeletionRecyclerViewAdapter.MyViewHolder>() {

    var mCheckedIds = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.delete_sale_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.titleDeletion.text = mData[position].itemTitle

        holder.checkBox.isChecked = mCheckedIds.get(position)

        holder.checkList.setOnClickListener {
            checkCheckBox(position,!mCheckedIds.get(position),holder.checkBox)
        }

    }

    fun checkCheckBox(pos:Int,value:Boolean,checkBox: CheckBox){
        if(value){
            checkBox.isChecked = true
            mCheckedIds.put(pos,true)
            mDeletionPos.add(pos)
        }else{
            checkBox.isChecked = false
            mCheckedIds.delete(pos)
            mDeletionPos.remove(pos)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var titleDeletion: TextView = itemView.findViewById(R.id.deleteListTxt)
        val checkList = itemView.findViewById<LinearLayout>(R.id.checkListItem)
        var checkBox = itemView.findViewById<CheckBox>(R.id.checkDeleteItem)


    }

}
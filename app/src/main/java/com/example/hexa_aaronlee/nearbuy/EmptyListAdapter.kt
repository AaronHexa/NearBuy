package com.example.hexa_aaronlee.nearbuy

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EmptyListAdapter(private val mContext: Context, private val mData: List<String>) : RecyclerView.Adapter<EmptyListAdapter.MyViewHolders>() {

    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        holder.txtSearch.text = mData[0]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {

        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.empty_list_item, parent, false)
        return MyViewHolders(view)
    }

        override fun getItemCount(): Int {
            return mData.size
        }

        class MyViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val txtSearch = itemView.findViewById<TextView>(R.id.emptyListTxt)
        }


}

package com.example.hexa_aaronlee.nearbuy

import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetail
import android.support.v7.widget.CardView
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(private val mDistance: String ,private val mContext: Context, private val mData: List<DealsDetail>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.card_view_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Picasso.get()
                .load(Uri.parse(mData[position].sales_image1))
                .centerCrop()
                .resize(700,700)
                .into(holder.imageSales)

        holder.title.text = mData[position].itemTitle
        holder.price.text = "MYR " + mData[position].itemPrice
        holder.offerBy.text = mData[position].offerBy
        holder.distance.text = mDistance

        holder.cardView.setOnClickListener {
            System.out.println("Clicked")
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var imageSales : ImageView
        lateinit var price : TextView
        lateinit var title : TextView
        lateinit var distance : TextView
        lateinit var offerBy : TextView
        lateinit var cardView: CardView

        init {

            imageSales = itemView.findViewById(R.id.salesPic)

            price = itemView.findViewById(R.id.priceSales)
            title = itemView.findViewById(R.id.titleSales)
            distance = itemView.findViewById(R.id.distanceSales)
            offerBy = itemView.findViewById(R.id.offerDealer)

            cardView = itemView.findViewById(R.id.cardView)
        }
    }


}
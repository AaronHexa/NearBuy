package com.example.hexa_aaronlee.nearbuy.Adapter

import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.hexa_aaronlee.nearbuy.DatabaseData.DealsDetailData
import android.support.v7.widget.CardView
import com.example.hexa_aaronlee.nearbuy.Activity.UserDetail
import com.example.hexa_aaronlee.nearbuy.Activity.ViewSaleDetailsActivity
import com.example.hexa_aaronlee.nearbuy.R
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(private val mContext: Context, private val mData: List<DealsDetailData>, private val userLatitude: Double, private val userLongitude:Double, private val fromMySale:Int) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

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
                .resize(700, 700)
                .into(holder.imageSales)

        holder.title.text = mData[position].itemTitle
        holder.price.text = "MYR " + mData[position].itemPrice
        holder.offerBy.text = mData[position].offerBy

        val result: FloatArray = FloatArray(10)

        Location.distanceBetween(userLatitude, userLongitude, mData[position].mLatitude.toDouble(), mData[position].mLongitude.toDouble(), result)
        holder.distance.text = String.format("%.2f", (result[0] / 1000)) + " km"
        holder.cardView.setOnClickListener {


            val intent = Intent(mContext, ViewSaleDetailsActivity::class.java)

            intent.putExtra("mySale",fromMySale)
            intent.putExtra("saleID",mData[position].sales_id)
            intent.putExtra("offerID",mData[position].offer_id)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var imageSales: ImageView
        lateinit var price: TextView
        lateinit var title: TextView
        lateinit var distance: TextView
        lateinit var offerBy: TextView
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
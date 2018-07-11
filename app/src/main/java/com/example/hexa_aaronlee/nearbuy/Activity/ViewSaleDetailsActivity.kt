package com.example.hexa_aaronlee.nearbuy.Activity

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.Presenter.ViewSaleDetailPresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.ViewSaleDetailView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_sale_details.*

class ViewSaleDetailsActivity : AppCompatActivity(), ViewSaleDetailView.View {

    lateinit var imageUri: Uri
    var tmpSaleTitle: String = ""
    var tmpSaleUser: String = ""
    var checkDealer: String = ""
    var result: FloatArray = FloatArray(10)
    lateinit var mPresenter: ViewSaleDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sale_details)

        mPresenter = ViewSaleDetailPresenter(this)

        val bundle = intent.extras
        UserDetail.saleSelectedId = bundle.getString("saleID")
        UserDetail.saleSelectedUserId = bundle.getString("offerID")

        mPresenter.getSalesDetail(UserDetail.saleSelectedId, UserDetail.saleSelectedUserId)


        floatingActionButton.setOnClickListener {
            Log.i("Sale id : ", UserDetail.saleSelectedId)
            if (checkDealer == UserDetail.user_id) {
                Toast.makeText(application, "You are the dealer !!!", Toast.LENGTH_SHORT).show()
            } else {
                UserDetail.chatWithID = checkDealer
                mPresenter.getChatDetail(checkDealer)
            }

        }
    }

    override fun updateUI(salesImage: String, itemTitle: String, itemPrice: String, itemDescription: String, itemLocation: String, mLatitude: String, mLongitude: String, offerBy: String, offer_id: String) {
        imageUri = Uri.parse(salesImage)

        Picasso.get()
                .load(imageUri)
                .centerCrop()
                .resize(700, 700)
                .into(imageProduct)

        titleDetail.text = itemTitle
        priceDetail.text = "MYR $itemPrice"
        descripeDetail.text = itemDescription
        locationDetail.text = itemLocation
        Location.distanceBetween(UserDetail.mLatitude, UserDetail.mLongitude, mLatitude.toDouble(), mLongitude.toDouble(), result)
        distanceDetail.text = String.format("%.2f", (result[0] / 1000)) + " km"
        offeredDetail2.text = offerBy

        checkDealer = offer_id
        tmpSaleTitle = itemTitle
        tmpSaleUser = offerBy
    }


    override fun updateInfo(profilePhoto: String, name: String, chatUserId: String) {
        UserDetail.chatWithImageUri = profilePhoto
        UserDetail.chatWithName = name
        UserDetail.chatWithID = chatUserId

        mPresenter.checkHistorySaleData(UserDetail.saleSelectedId, UserDetail.user_id)
    }

    override fun saveHistoryData(checkedResult: Boolean) {
        if (checkedResult) {

            mPresenter.saveUserToHistoryChat(UserDetail.imageUrl, UserDetail.user_id, checkDealer, UserDetail.username, tmpSaleTitle, UserDetail.saleSelectedId, tmpSaleUser, UserDetail.chatWithImageUri)

            //finish()
            startActivity(Intent(applicationContext, ChatRoomActivity::class.java))

        } else if (!checkedResult) {

            //finish()
            startActivity(Intent(applicationContext, ChatRoomActivity::class.java))
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

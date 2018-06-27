package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.Presenter.ViewSaleDetailPresenter
import com.example.hexa_aaronlee.nearbuy.View.ViewSaleDetailView
import com.google.firebase.database.*
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

        mPresenter.getSalesDetail(UserDetail.saleSelectedId)


        floatingActionButton.setOnClickListener {
            if (checkDealer == UserDetail.user_id) {
                Toast.makeText(application, "You are the dealer !!!", Toast.LENGTH_SHORT).show()
            } else {
                UserDetail.chatWithID = checkDealer
                mPresenter.getChatDetail(checkDealer)
                finish()
                startActivity(Intent(applicationContext, ChatRoomActivity::class.java))
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


    override fun updateInfo(profilePhoto: String, name: String) {
        UserDetail.chatWithImageUri = profilePhoto
        UserDetail.chatWithName = name

        mPresenter.saveUserToHistoryChat(profilePhoto, UserDetail.user_id, checkDealer, tmpSaleUser, tmpSaleTitle)
    }

    override fun onBackPressed() {

        finish()
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
        super.onBackPressed()
    }
}

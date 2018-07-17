package com.example.hexa_aaronlee.nearbuy.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.Adapter.EmptyListAdapter
import com.example.hexa_aaronlee.nearbuy.Presenter.ChatHistoryPresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.R.layout.list_view_design
import com.example.hexa_aaronlee.nearbuy.View.ChatHistoryView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_history.*
import kotlin.collections.ArrayList

class ChatHistoryActivity : AppCompatActivity(), ChatHistoryView.View {

    lateinit var historyData: ArrayList<String>
    lateinit var nameData: ArrayList<String>
    lateinit var imageData: ArrayList<String>
    lateinit var titleData: ArrayList<String>
    lateinit var saleData: ArrayList<String>
    lateinit var msg_status: ArrayList<String>
    lateinit var msg_statusCount: ArrayList<Int>
    lateinit var chatListKeyArray: ArrayList<String>

    lateinit var mPresenter: ChatHistoryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)

        mPresenter = ChatHistoryPresenter(this)

        historyData = ArrayList()
        imageData = ArrayList()
        nameData = ArrayList()
        titleData = ArrayList()
        saleData = ArrayList()
        msg_status = ArrayList()
        msg_statusCount = ArrayList()
        chatListKeyArray = ArrayList()

        val actionBar = this.supportActionBar!!

        actionBar.title = "Chat List"
        actionBar.setDisplayHomeAsUpEnabled(true)


        mPresenter.checkChatHistiryData(UserDetail.user_id)

        Log.i("Test Run : ", "run")
    }

    override fun setRecyclerViewAdapter(historyData: ArrayList<String>,
                                        imageData: ArrayList<String>,
                                        nameData: ArrayList<String>,
                                        titleData: ArrayList<String>,
                                        saleData: ArrayList<String>,
                                        msg_status: ArrayList<String>,
                                        msg_statusCount: ArrayList<Int>,
                                        chatListKeyArray: ArrayList<String>) {

        val customAdapter = CustomListView(applicationContext, nameData, historyData, imageData, titleData, saleData, msg_status, msg_statusCount, chatListKeyArray)
        listView.layoutManager = LinearLayoutManager(applicationContext)
        listView.adapter = customAdapter
    }

    override fun setEmptyViewAdapter(checkDataExits: Boolean) {

        if (!checkDataExits) {

            val arrayData = ArrayList<String>()
            arrayData.add("No Chat History!")
            val emptyAdapter = EmptyListAdapter(applicationContext, arrayData)
            listView.layoutManager = LinearLayoutManager(applicationContext)
            listView.setHasFixedSize(true)
            listView.adapter = emptyAdapter

        } else {

            mPresenter.getChatHistoryDataFromDatabase(historyData, imageData, nameData, titleData, UserDetail.user_id, saleData, msg_status, msg_statusCount, chatListKeyArray)

            this.historyData = ArrayList() //refresh all arrayList ( will not repeat)
            this.imageData = ArrayList()
            this.nameData = ArrayList()
            this.titleData = ArrayList()
            this.saleData = ArrayList()
            this.msg_status = ArrayList()
            this.msg_statusCount = ArrayList()
            this.chatListKeyArray = ArrayList()
        }
    }


    inner class CustomListView(private val context: Context,
                               private val nameSource: ArrayList<String>,
                               private val IdSource: ArrayList<String>,
                               private val imageData: ArrayList<String>,
                               private val titleData: ArrayList<String>,
                               private val saleData: ArrayList<String>,
                               private val msg_status: ArrayList<String>,
                               private val msg_statusCount: ArrayList<Int>,
                               private val chatListKeyArray: ArrayList<String>) : RecyclerView.Adapter<CustomListView.CustomViewHolder>() {

        private var mContext: Context = context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(list_view_design, parent, false)
            return CustomViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return nameSource.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

            holder.listTxt?.text = nameSource[position]

            val tmpUri = Uri.parse(imageData[position])

            Picasso.get()
                    .load(tmpUri)
                    .centerCrop()
                    .resize(700, 700)
                    .into(holder.listImage)

            holder.listTitle?.text = titleData[position]

            if (msg_status[position] == "New") {
                holder.notifyStatusCount!!.visibility = TextView.VISIBLE

                holder.notifyStatusCount!!.text = msg_statusCount[position].toString()
            }

            holder.onClickMethod(position, nameSource, IdSource, imageData, saleData, mContext, chatListKeyArray)

        }


        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var listTxt: TextView? = null
            var listImage: ImageView? = null
            var listTitle: TextView? = null
            var notifyStatusCount: TextView? = null
            lateinit var databaseRef: DatabaseReference
            var historyUser = ""
            var saleId = ""
            var history_userName = ""
            var history_image = ""
            var history_title = ""


            init {
                listTxt = itemView.findViewById(R.id.list_txt)
                listImage = itemView.findViewById(R.id.list_icon)
                listTitle = itemView.findViewById(R.id.dealTitleTxt)
                notifyStatusCount = itemView.findViewById(R.id.msgStatusCountTxt)

            }

            fun onClickMethod(pos: Int, nameSource: ArrayList<String>,
                              IdSource: ArrayList<String>,
                              imageData: ArrayList<String>,
                              saleData: ArrayList<String>,
                              mContext: Context,
                              chatListKeyArray: ArrayList<String>) {

                itemView.setOnClickListener {
                    Toast.makeText(itemView.context, "Position : $position", Toast.LENGTH_SHORT).show()

                    UserDetail.chatWithID = IdSource[pos]
                    UserDetail.chatWithName = nameSource[pos]
                    UserDetail.chatWithImageUri = imageData[pos]
                    UserDetail.saleSelectedId = saleData[pos]
                    UserDetail.chatListKey = chatListKeyArray[pos]


                    getHistoryData(UserDetail.user_id, UserDetail.chatListKey)

                    val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    itemView.context.startActivity(intent)

                }

            }

            fun getHistoryData(user_id: String, chatListKey: String) {
                databaseRef = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id).child(chatListKey)

                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.getValue(HistoryData::class.java)!!

                        historyUser = data.history_user
                        saleId = data.sale_id
                        history_userName = data.history_userName
                        history_image = data.history_image
                        history_title = data.history_title


                        saveMsgStatus(user_id, chatListKey)

                    }

                })
            }

            fun saveMsgStatus(user_id: String, chatListKey: String) {
                val databaseRef2 = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(user_id).child(chatListKey)


                val data = HistoryData(historyUser, saleId, history_userName, history_image, history_title, "Old", 0, chatListKey)

                databaseRef2.setValue(data)

            }

        }

    }


    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        //startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
    }
}



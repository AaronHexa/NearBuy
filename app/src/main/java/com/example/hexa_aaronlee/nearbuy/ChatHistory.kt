package com.example.hexa_aaronlee.nearbuy

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.R.layout.list_view_design
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_history.*
import kotlin.collections.ArrayList

class ChatHistory : AppCompatActivity() {

    lateinit var historyData: ArrayList<String?>
    lateinit var nameData: ArrayList<String?>
    lateinit var imageData: ArrayList<String?>
    lateinit var databaseR: DatabaseReference
    lateinit var databaseR2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)

        historyData = ArrayList()
        imageData = ArrayList()
        nameData = ArrayList()

        getChatHistoryData()

    }

    fun getChatHistoryData() {
        var tmpNum: Int = 0
        var tmpString: String? = null
        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(UserDetail.user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)
                    historyData.add(data?.history_user)
                    nameData.add(data?.history_userName)

                    System.out.println("..................." + data?.history_user)

                    tmpNum += 1
                    tmpString = tmpNum.toString()
                    System.out.println(tmpString + "............" + dataSnapshot.childrenCount.toString())

                    if (tmpString.equals(dataSnapshot.childrenCount.toString())) {
                        val customAdapter = CustomListView(applicationContext, nameData, historyData, imageData)
                        listView.layoutManager = LinearLayoutManager(applicationContext)
                        listView.adapter = customAdapter
                        System.out.println(tmpNum)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }


    class CustomListView(private val context: Context,
                         private val nameSource: ArrayList<String?>,
                         private val IdSource: ArrayList<String?>,
                         private val imageData: ArrayList<String?>) : RecyclerView.Adapter<CustomListView.CustomViewHolder>() {

        private var mContext: Context = context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):CustomViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(list_view_design, parent, false)
            return CustomViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return nameSource.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

            holder.listTxt?.text = nameSource[position]

            holder.onClickMethod(position, nameSource, IdSource, imageData)

        }


        class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var listTxt: TextView? = null
            var listImage: ImageView? = null


            init {
                listTxt = itemView.findViewById(R.id.list_txt)
                listImage = itemView.findViewById(R.id.list_icon)

            }

            fun onClickMethod(pos: Int, nameSource: ArrayList<String?>,
                              IdSource: ArrayList<String?>,
                              imageData: ArrayList<String?>) {
                itemView.setOnClickListener {
                    Toast.makeText(itemView.context, "Position : $position", Toast.LENGTH_SHORT).show()

                    UserDetail.chatWithID = IdSource[pos]
                    UserDetail.chatWithName = nameSource[pos]

                    System.out.println(UserDetail.chatWithID + "................." + UserDetail.chatWithName)

                    val intent = Intent(itemView.context, ChatRoom::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    itemView.context.startActivity(intent)
                }
            }

        }

    }
}

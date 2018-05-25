package com.example.hexa_aaronlee.nearbuy

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.hexa_aaronlee.nearbuy.DatabaseData.HistoryData
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_history.*
import kotlinx.android.synthetic.main.list_view_design.view.*
import java.util.*

class ChatHistory : AppCompatActivity() {

    lateinit var historyData : ArrayList<String?>
    lateinit var nameData : ArrayList<String?>
    lateinit var imageData : ArrayList<String?>
    lateinit var databaseR :DatabaseReference
    lateinit var databaseR2 :DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)

        historyData = ArrayList()
        imageData = ArrayList()
        nameData = ArrayList()

        getChatHistoryData()

        listView.setOnItemClickListener { parent, view, position, id ->
            System.out.println(position)

            UserDetail.chatWithID = historyData[position]
            UserDetail.chatWithName = nameData[position]

            System.out.println(UserDetail.chatWithID + "................."+ UserDetail.chatWithName)

            startActivity(Intent(applicationContext,ChatRoom::class.java))
            finish()
        }


    }

    fun getChatHistoryData()
    {
        var tmpNum : Int = 0
        var tmpString : String? = null
        databaseR = FirebaseDatabase.getInstance().reference.child("TotalHistory").child(UserDetail.user_id)


        databaseR.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val data = postSnapshot.getValue(HistoryData::class.java)
                    historyData.add(data?.history_user)

                    System.out.println("..................." + data?.history_user)

                    tmpNum += 1
                    tmpString = tmpNum.toString()
                    System.out.println(tmpString +"............"+dataSnapshot.childrenCount.toString())

                    if(tmpString.equals(dataSnapshot.childrenCount.toString())) {
                        getNameData(historyData)
                        System.out.println(tmpNum)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun getNameData(historyData: ArrayList<String?>)
    {
        nameData.clear()

        for(i in 0 .. historyData.count()-1) {
            databaseR2 = FirebaseDatabase.getInstance().reference.child("User").child(historyData[i])

            databaseR2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(UserData::class.java)
                    nameData.add("$i."+data?.name)

                    System.out.println("......data UserName........." + data?.name)

                    System.out.println("$i ....Number of i")

                    if(i == 0)
                    {
                        val customAdapter = CustomListView(applicationContext,nameData,imageData)
                        listView.adapter = customAdapter
                        System.out.println("Hereee")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })

        }
    }

    class CustomListView(private val context: Context,
                        private val dataSource: ArrayList<String?>,
                         private val imageData: ArrayList<String?>) : BaseAdapter() {

        private var mContext :Context = context

        override fun getCount(): Int {
            return dataSource.size
        }

        //2
        override fun getItem(position: Int): String? {
            return dataSource[position]
        }

        //3
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        //4
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Get view for row item
            val layoutInflater = LayoutInflater.from(mContext)
            val rowView = layoutInflater.inflate(R.layout.list_view_design, parent, false)

            rowView.list_txt.text = dataSource[position]
            System.out.println(dataSource[position])
            rowView.list_icon.setImageResource(R.drawable.ic_person_black_24dp)

            return rowView
        }

    }
}

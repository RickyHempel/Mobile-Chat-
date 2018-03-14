package com.example.ricky.friendsactivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var ab= supportActionBar
        ab?.setTitle("FireChat1")
        ab?.setSubtitle(("Chat Room"))

        ab?.setDisplayHomeAsUpEnabled(true)

        if (this.intent.hasExtra("userEmail")){
            mUserEmail= this.intent.getStringExtra("userEmail")
            mUserImageUrl = this.intent.getStringExtra("userImageUrl")
        }
        else{
            Log.w("debug","Activity requires a logged in user")
        }

        attachRecyclerView()

        chatService.setupService(recyclerview.context,{ message -> addMessageToRecyclerView(message)})

        buttonSend.setOnClickListener({view -> sendMessage()})
    }

    private var mUserEmail:String=""
    private var mUserImageUrl:String=""

    private val chatService=FireChatService.instance

    private fun sendMessage(){
        chatService.sendMessage(mUserEmail,mUserImageUrl,sendText.text.toString())
    }
    private fun addMessageToRecyclerView(message: ChatData?){
        if( message!=null){
            val cellData=CellData(message.fromEmail,message.fromImageURL,message.message)
            addCellToRecyclerView(cellData)
            sendText.setText("")
        }
    }

    lateinit var adapter: CellViewAdapter
    private fun attachRecyclerView(){
        val manager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = manager
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        initiliizeRecyclerView()
    }

    private fun initiliizeRecyclerView(){
        adapter = CellViewAdapter{view, position-> rowTapped(position)}
        recyclerview.adapter = adapter
    }

    private fun rowTapped (position: Int){
        Log.d("debug",adapter.rows[position].headerTxt+" " + adapter.rows[position].messageText)
    }

    private fun addCellToRecyclerView(cellData: CellData){
        adapter.addCellData(cellData)
        recyclerview.smoothScrollToPosition(adapter.getCellCount()-1)
    }
}

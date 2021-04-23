package ru.nsu.trivia.quiz

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.quiz.adapters.PlayerInfo
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter

class LobbyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        fillRecyclerView()
        if (intent.extras?.getBoolean("isHost") == false){
           findViewById<Button>(R.id.button_start_game).visibility = View.INVISIBLE
        }
    }

    private fun getRoomUsers(): List<PlayerInfo>{
        //TODO: get this list from the server
        val info = ArrayList<PlayerInfo>()
        info.add(PlayerInfo(intent.extras?.get("userName") as String, true))
        info.add(PlayerInfo("Vasyan97", false))
        info.add(PlayerInfo("Katushka2001", false))
        return info
    }

    private fun fillRecyclerView(){
        val adapter = PlayerRecyclerViewAdapter(this, getRoomUsers())
        val mLayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_lobby)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = adapter

    }
}
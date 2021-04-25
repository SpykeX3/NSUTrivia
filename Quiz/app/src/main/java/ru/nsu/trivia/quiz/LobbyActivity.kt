package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter

class LobbyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        fillRecyclerView()

        if (intent.extras?.getBoolean("isHost") == false){
           findViewById<Button>(R.id.button_start_game).visibility = View.INVISIBLE
        }

        findViewById<Button>(R.id.button_start_game).setOnClickListener{ view ->
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getRoomUsers(): List<PlayerInLobby>{
        //TODO: get this list from the server
        val info = ArrayList<PlayerInLobby>()
        info.add(PlayerInLobby(intent.extras?.get("userName") as String, true))
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
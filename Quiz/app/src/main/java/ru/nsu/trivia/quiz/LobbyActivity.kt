package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter

class LobbyActivity : AppCompatActivity() {
    private lateinit var lobbyDTO: LobbyDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val objectMapper = ObjectMapper()
        lobbyDTO = intent.extras?.getString("lobbyDTO")?.let { objectMapper.readValue<LobbyDTO>(it) }!!

        fillRecyclerView()

        if (intent.extras?.getBoolean("isHost") == false){
           findViewById<Button>(R.id.button_start_game).visibility = View.INVISIBLE
        }


        if (lobbyDTO == null){
            //TODO: и вот чё это за фигня? это не возможно, но надо бы придумать, как с этим жить, если вдруг баганёт
        }

        findViewById<TextView>(R.id.room_code_text_view).text = lobbyDTO.id

        findViewById<Button>(R.id.button_start_game).setOnClickListener{ view ->
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getRoomUsers(): List<PlayerInLobby>{
        return lobbyDTO.players
    }

    fun shareRoomCode(view: View){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, lobbyDTO.id)
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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
package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.AsyncTask
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
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController

class LobbyActivity : AppCompatActivity() {
    private lateinit var lobbyDTO: LobbyDTO
    private val context = this
    private lateinit var adapter: PlayerRecyclerViewAdapter

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
        RoomSubscriber().execute()
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
        val response = ArrayList<PlayerInLobby>()
        response.addAll(getRoomUsers())
        adapter = PlayerRecyclerViewAdapter(this, response)
        val mLayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_lobby)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = adapter
    }

    private inner class RoomSubscriber: AsyncTask<Void, Integer, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            //TODO: start game
            if (lobbyDTO != null) {
                RoomSubscriber().execute()
                adapter.notifyDataSetChanged()
            }
        }

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            Thread.sleep(500);
            val request = UsingTokenRequest()
            request.token = TokenController.getToken(context)
            val lobby = APIConnector.doPost("lobby/get", request)
            if (!lobby.equals("")) {
                val objectMapper = ObjectMapper()
                lobbyDTO = objectMapper.readValue<LobbyDTO>(lobby)
                adapter.setResponseListToNew(lobbyDTO.players)
                return lobbyDTO
            }
            return null
        }
    }

    private inner class LeaveRoom: AsyncTask<Void, Integer, LobbyDTO?>() {

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            val request = UsingTokenRequest()
            request.token = TokenController.getToken(context)
            APIConnector.doPost("lobby/leave", request)
            return null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LeaveRoom().execute()
    }
}
package ru.nsu.trivia.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.LobbyState
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import ru.nsu.trivia.quiz.gameFragments.InRoomActivity
import ru.nsu.trivia.quiz.gameFragments.TaskController
import ru.nsu.trivia.quiz.tasks.AlertDialogCreator
import java.util.concurrent.Executors

class LobbyActivity : InRoomActivity(){
    private val context = this
    private lateinit var adapter: PlayerRecyclerViewAdapter
    private val exec = Executors.newFixedThreadPool(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val objectMapper = ObjectMapper()
        lobby =
            intent.extras?.getString("lobbyDTO")?.let { objectMapper.readValue<LobbyDTO>(it) }!!

        fillRecyclerView()

        if (intent.extras?.getBoolean("isHost") == false) {
            findViewById<Button>(R.id.button_start_game).visibility = View.INVISIBLE
        }

        findViewById<TextView>(R.id.room_code_text_view).text = lobby.id
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.INVISIBLE

        findViewById<Button>(R.id.button_start_game).setOnClickListener { view ->
            StartGameTask().executeOnExecutor(exec)
        }
        RoomSubscriber().execute()
    }

    private fun getRoomUsers(): List<PlayerInLobby> {
        return lobby.players
    }

    fun shareRoomCode(view: View) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, lobby.id)
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


    private fun fillRecyclerView() {
        val response = ArrayList<PlayerInLobby>()
        response.addAll(getRoomUsers())
        adapter = PlayerRecyclerViewAdapter(this, response)
        val mLayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_lobby)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = adapter
    }

    private inner class StartGameTask : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            Thread.sleep(500)
            APIConnector.doPost("lobby/start", TokenController.getToken(context))
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class RoomSubscriber : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            if (lobby.state == LobbyState.Playing){
                val controller = TaskController(this@LobbyActivity)
                controller.goToTaskActivity(lobby)
            } else if (lobby.state == LobbyState.Waiting) {
                RoomSubscriber().execute()
                adapter.notifyDataSetChanged()
            }
            else if (lobby.state == LobbyState.Closed || lobby.state == LobbyState.Finished){
             //Todo: host left the game
            }
        }

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            val result =
                APIConnector.doLongPoling("lobby/subscribe", TokenController.getToken(context), lobby.lastUpdated)
            if (result.code == 200) {
                val objectMapper = ObjectMapper()
                lobby = objectMapper.readValue<LobbyDTO>(result.responce)
                adapter.setResponseListToNew(lobby.players)
                return lobby
            }
            else{
                //TODO
            }
            return null
        }
    }

    override fun onBackPressed() {
        AlertDialogCreator().createAlertDialog(this).show()
    }
}
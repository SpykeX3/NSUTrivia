package ru.nsu.trivia.quiz.gameFragments

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.LobbyState
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.MenuActivity
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.ResultsRecyclerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import kotlin.properties.Delegates

class ResultsActivity : AppCompatActivity() {

    lateinit var adapter: ResultsRecyclerViewAdapter
    private lateinit var lobby: LobbyDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        val objectMapper = ObjectMapper()
        lobby =
            intent.extras?.getString("LobbyDTO")?.let { objectMapper.readValue<LobbyDTO>(it) }!!
        fillRecyclerView()

        findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
        findViewById<TextView>(R.id.round_num_text_view).setText("Round " + (lobby.round - 1))
        adapter.notifyDataSetChanged()

        if (lobby.state == LobbyState.Closed) {
            adapter.showWinner()
            adapter.notifyDataSetChanged()
            findViewById<TextView>(R.id.round_num_text_view).setText("Game over")
            findViewById<Button>(R.id.leave_game).visibility = View.VISIBLE
        }

        GOTO().execute()
    }

    fun leaveGame(view: View){
        LeaveLobby().execute()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    inner class LeaveLobby : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int {
            val request = UsingTokenRequest()
            request.token = TokenController.getToken(this@ResultsActivity)
            APIConnector.doPost("lobby/leave", request)
            return 0
        }
    }

    private fun fillRecyclerView() {
        val response = ArrayList<PlayerInLobby>()
        response.addAll(lobby.players)
        adapter = ResultsRecyclerViewAdapter(this, response)
        val mLayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.result_view_results)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = adapter
    }

    inner class GOTO : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int {
            Thread.sleep(5000)
            return 0
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            if (lobby.state == LobbyState.Playing) {
                val controller = TaskController(this@ResultsActivity)
                controller.goToTaskActivity(lobby)
            }
        }
    }
}
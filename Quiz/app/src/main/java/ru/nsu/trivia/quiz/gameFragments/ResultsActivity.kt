package ru.nsu.trivia.quiz.gameFragments

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.PlayerRecyclerViewAdapter
import ru.nsu.trivia.quiz.adapters.ResultsRecyclerViewAdapter
import ru.nsu.trivia.quiz.adapters.SelectAnswerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import kotlin.properties.Delegates

class ResultsActivity : AppCompatActivity() {

    lateinit var adapter: ResultsRecyclerViewAdapter
    private var mLayoutManager = LinearLayoutManager(this)
    private lateinit var lobby: LobbyDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        val objectMapper = ObjectMapper()
        lobby =
            intent.extras?.getString("LobbyDTO")?.let { objectMapper.readValue<LobbyDTO>(it) }!!
        fillRecyclerView()

        WaitForUpdate().execute()
    }

    private fun fillRecyclerView() {
        val response = ArrayList<PlayerInLobby>()
        //response.addAll(lobby.players)
        adapter = ResultsRecyclerViewAdapter(this, response)
        val mLayoutManager = LinearLayoutManager(this)
        (mLayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.result_view_results)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = adapter
    }

    inner class WaitForUpdate : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int {
            val result = APIConnector.doLongPoling(
                "lobby/subscribe",
                TokenController.getToken(this@ResultsActivity),
                lobby.lastUpdated
            )

            if (result.code == 200) {
                val objectMapper = ObjectMapper()
                lobby = objectMapper.readValue<LobbyDTO>(result.responce)
                adapter.responseList.addAll(ArrayList(lobby.players))
            } else {
                //TODO
            }
            return 0
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.round_num_text_view).setText("Round " + (lobby.round - 1))
            adapter.notifyDataSetChanged()
            GOTO().execute()
        }
    }

    inner class GOTO : AsyncTask<Int, Int, Int>() {

        override fun doInBackground(vararg params: Int?): Int {
            Thread.sleep(5000)
            return 0
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            val controller = TaskController(this@ResultsActivity)
            controller.goToTaskActivity(lobby)
        }
    }
}
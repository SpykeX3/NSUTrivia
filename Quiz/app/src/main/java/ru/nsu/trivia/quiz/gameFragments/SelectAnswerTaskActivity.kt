package ru.nsu.trivia.quiz.gameFragments

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.LobbyState
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.SelectAnswerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class SelectAnswerTaskActivity : Activity() {

    lateinit var task: SelectAnswerTaskDTO
    lateinit var adapter: SelectAnswerViewAdapter
    private var mLayoutManager = LinearLayoutManager(this)
    private lateinit var lobby: LobbyDTO
    private var currRound by Delegates.notNull<Int>()
    private var isAnswered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_answer_task)

        if (intent != null) {
            val objectMapper = ObjectMapper()
            val extra = intent.getStringExtra("LobbyDTO")
            lobby = extra?.let { objectMapper.readValue<LobbyDTO>(it) }!!
            currRound = lobby.round
        }

        //init question
        (mLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_variants)
        mRecyclerView?.layoutManager = mLayoutManager
        task = lobby.currentTask as SelectAnswerTaskDTO
        adapter = SelectAnswerViewAdapter(this, task)
        mRecyclerView?.adapter = adapter

        findViewById<TextView>(R.id.text_view_question).text = task.question
        val animation = findViewById<LottieAnimationView>(R.id.animationView)
        animation.visibility = View.INVISIBLE

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = lobby.currentTask.timeLimit / 1000
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (!isAnswered) {
                    if (Math.abs(System.currentTimeMillis() - time) < lobby.currentTask.timeLimit) {
                        progressBar.progress =
                            Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                        Log.d(
                            "Tag",
                            (Math.abs(System.currentTimeMillis() - time).toInt() / 1000).toString()
                        )
                        handler.postDelayed(this, 1000)
                    }
                } else {
                    animation.visibility = View.VISIBLE
                    animation.focusable = View.FOCUSABLE
                }
            }
        })

        RoomSubscriber().execute()
    }


    fun goToLobbyResult() {
        val intent = Intent(this, ResultsActivity::class.java)
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(lobby)
        intent.putExtra("LobbyDTO", json)
        startActivity(intent)
    }


    fun showCorrect(id: Int) {
        adapter.showCorrect(id, (lobby.currentTask as SelectAnswerTaskDTO).correctVariantId)
        adapter.notifyDataSetChanged()
        val exec = Executors.newFixedThreadPool(2)
        SendCorrectAns().executeOnExecutor(exec, id)
        findViewById<LottieAnimationView>(R.id.animationView).visibility = View.VISIBLE
    }

    inner class SendCorrectAns : AsyncTask<Int, Int, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
            isAnswered = true
        }

        override fun doInBackground(vararg params: Int?): Int {
            val answer = SelectAnswerAnswer()
            answer.variantId = params[0]!!
            answer.round = currRound
            answer.token = TokenController.getToken(this@SelectAnswerTaskActivity)
            APIConnector.doPost("lobby/answer", answer)
            return 0
        }
    }

    private inner class RoomSubscriber : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            goToLobbyResult()
        }

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            val result =
                APIConnector.doLongPoling(
                    "lobby/subscribe",
                    TokenController.getToken(this@SelectAnswerTaskActivity),
                    lobby.lastUpdated
                )
            if (result.code == 200) {
                val objectMapper = ObjectMapper()
                lobby = objectMapper.readValue<LobbyDTO>(result.responce)
                return lobby
            } else {
                //TODO
            }
            return null
        }
    }
}
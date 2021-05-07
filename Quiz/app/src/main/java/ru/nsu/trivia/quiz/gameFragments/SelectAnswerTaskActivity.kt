package ru.nsu.trivia.quiz.gameFragments

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.SelectAnswerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import kotlin.properties.Delegates

class SelectAnswerTaskActivity : Activity() {

    lateinit var task: SelectAnswerTaskDTO
    lateinit var adapter: SelectAnswerViewAdapter
    private var mLayoutManager = LinearLayoutManager(this)
    private lateinit var lobby: LobbyDTO
    private var currRound by Delegates.notNull<Int>()

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

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 10
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (Math.abs(System.currentTimeMillis() - time) < 10 * 1000) {
                    progressBar.progress =
                        Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                    handler.postDelayed(this, 1000)
                    println(-System.currentTimeMillis() + time)
                } else {
                    goToLobbyResult()
                }
            }
        })
    }

    fun goToLobbyResult() {
        //TODO: SHOW RESULTS AFTER ROUND
    }

    fun showCorrect(id: Int) {
        adapter.showCorrect(id, (lobby.currentTask as SelectAnswerTaskDTO).correctVariantId)
        adapter.notifyDataSetChanged()
        SendCorrectAns().execute(id)
    }

    inner class SendCorrectAns : AsyncTask<Int, Int, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            val answer = SelectAnswerAnswer()
            answer.variantId = params[0]!!
            answer.round = currRound
            answer.token = TokenController.getToken(this@SelectAnswerTaskActivity)
            APIConnector.doPost("lobby/answer", answer)
            return 0
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            goToLobbyResult()
        }

    }
}
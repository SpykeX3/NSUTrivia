package ru.nsu.trivia.quiz.gameFragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.SelectAnswerViewAdapter
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import ru.nsu.trivia.quiz.tasks.AlertDialogCreator
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class SelectAnswerInRoomActivity : InRoomActivity() {

    lateinit var task: SelectAnswerTaskDTO
    lateinit var adapter: SelectAnswerViewAdapter
    private var mLayoutManager = LinearLayoutManager(this)
    private var currRound by Delegates.notNull<Int>()
    private var isAnswered = false
    private var timeOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_answer_task)
        if (intent != null) {
            val objectMapper = ObjectMapper()
            val extra = intent.getStringExtra("LobbyDTO")
            lobby = extra?.let { objectMapper.readValue<LobbyDTO>(it) }!!
            currRound = lobby.round
        }

        (mLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_variants)
        mRecyclerView?.layoutManager = mLayoutManager
        task = lobby.currentTask as SelectAnswerTaskDTO
        adapter = SelectAnswerViewAdapter(this, task)
        mRecyclerView?.adapter = adapter

        findViewById<TextView>(R.id.text_view_question).text = task.question
        val animation = findViewById<LottieAnimationView>(R.id.animationWaitingView)
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.INVISIBLE
        animation.visibility = View.INVISIBLE

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = lobby.currentTask.timeLimit / 1000
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (Math.abs(System.currentTimeMillis() - time) < lobby.currentTask.timeLimit) {
                    progressBar.progress =
                        Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                    if (!isAnswered) {
                        Log.d(
                            "Tag",
                            (Math.abs(System.currentTimeMillis() - time).toInt() / 1000).toString()
                        )
                        handler.postDelayed(this, 1000)
                    }
                } else {
                    timeOut = true
                    animation.visibility = View.VISIBLE
                    animation.focusable = View.FOCUSABLE
                    progressBar.progress =
                        Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                    if (!isAnswered)
                        SendCorrectAns().executeOnExecutor(Executors.newFixedThreadPool(2))
                }
            }
        })
        RoomSubscriber().execute()
    }

    fun showCorrect(id: Int) {
        adapter.showCorrect(id, (lobby.currentTask as SelectAnswerTaskDTO).correctVariantId)
        adapter.notifyDataSetChanged()
        val exec = Executors.newFixedThreadPool(2)
        SendCorrectAns().executeOnExecutor(exec, id)
        findViewById<LottieAnimationView>(R.id.animationWaitingView).visibility = View.VISIBLE
    }

    inner class SendCorrectAns : AsyncTask<Int, Int, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
            isAnswered = true
        }

        override fun doInBackground(vararg params: Int?): Int {
            val answer = SelectAnswerAnswer()
             if (!timeOut) {
                answer.variantId = params[0]!!
            }else{
                answer.variantId = Integer.MIN_VALUE
            }
            answer.round = currRound
            answer.token = TokenController.getToken(this@SelectAnswerInRoomActivity)
            APIConnector.doPost("lobby/answer", answer)
            return 0
        }
    }

    override fun onBackPressed() {
        AlertDialogCreator().createAlertDialog(this).show()
    }
}
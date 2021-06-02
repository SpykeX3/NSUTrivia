package ru.nsu.trivia.quiz.gameFragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.task.SetNearestValueAnswer
import ru.nsu.trivia.common.dto.model.task.SetNearestValueTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import ru.nsu.trivia.quiz.tasks.AlertDialogCreator
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class SetNearestAnswerActivity : InRoomActivity() {
    lateinit var task: SetNearestValueTaskDTO
    private var isAnswered = false
    private var timeOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nearest_answer)

        if (intent != null) {
            val objectMapper = ObjectMapper()
            val extra = intent.getStringExtra("LobbyDTO")
            lobby = extra?.let { objectMapper.readValue<LobbyDTO>(it) }!!
            currRound = lobby.round
            task = lobby.currentTask as SetNearestValueTaskDTO
        }

        findViewById<TextView>(R.id.text_view_question).text = task.question
        findViewById<LinearLayout>(R.id.error).visibility = View.INVISIBLE

        val animation = findViewById<LottieAnimationView>(R.id.animationWaitingView)
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
                    if (!timeOut) {
                        handler.postDelayed(this, 500)
                    }
                } else {
                    progressBar.progress =
                        Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                    animation.visibility = View.VISIBLE
                    animation.focusable = View.FOCUSABLE
                    timeOut = true
                    if (!timeOut) {
                        SendCorrectAns().executeOnExecutor(Executors.newFixedThreadPool(2))
                    }
                }
            }
        })
        RoomSubscriber().execute()

        findViewById<EditText>(R.id.edit_text).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    setButtonActive()
                } else {
                    setButtonInactive()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun showCorrect() {
        findViewById<TextView>(R.id.text_view_question).text =
            findViewById<TextView>(R.id.text_view_question).text.toString() +
                    "\n Correct answer: " + task.correctAnswer
    }

    @SuppressLint("StaticFieldLeak")
    inner class SendCorrectAns : AsyncTask<Int, Int, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<LottieAnimationView>(R.id.animationWaitingView).visibility = View.VISIBLE
            isAnswered = true
            showCorrect()
        }

        override fun doInBackground(vararg params: Int?): Int {
            val answer = SetNearestValueAnswer()
            if (!timeOut) {
                answer.answer =
                    Integer.parseInt(findViewById<EditText>(R.id.edit_text).text.toString())
            } else {
                answer.answer = Integer.MIN_VALUE
            }
            answer.round = currRound
            answer.token = TokenController.getToken(this@SetNearestAnswerActivity)
            Thread.sleep(1000)
            APIConnector.doPost("lobby/answer", answer)
            return 0
        }
    }

    private fun setButtonActive() {
        findViewById<Button>(R.id.send_answer).setTextColor(getColor(R.color.green_700))
    }

    private fun setButtonInactive() {
        findViewById<Button>(R.id.send_answer).setTextColor(getColor(R.color.green_200))
    }


    fun setAnswer(view: View) {
        if (findViewById<EditText>(R.id.edit_text).text.isNotEmpty() && !isAnswered) {
            val exec = Executors.newFixedThreadPool(2)
            SendCorrectAns().executeOnExecutor(exec)
        }
    }

    override fun onBackPressed() {
        AlertDialogCreator().createAlertDialog(this).show()
    }
}

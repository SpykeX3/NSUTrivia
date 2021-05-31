package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest
import ru.nsu.trivia.common.dto.requests.JoinLobbyRequest
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.ConnectionResult
import ru.nsu.trivia.quiz.clientTasks.TokenController

class JoinRoomActivity : AppCompatActivity() {
    val context = this
    lateinit var playerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)
        findViewById<EditText>(R.id.edit_text_room_code).addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 6) {
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.green_700))
                } else {
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.green_200))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        playerName = intent.extras?.getString("userName").toString()

        findViewById<Button>(R.id.join_room).setOnClickListener { view ->
            joinRoom(findViewById<EditText>(R.id.edit_text_room_code).text.toString())
        }
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.INVISIBLE
    }

    private fun joinRoom(roomCode: String) {
        if (roomCode.length == 6) {
            RoomJoiner().execute()
        }
    }

    private fun showButtons(value: Int) {
        findViewById<Button>(R.id.join_room).visibility = value
    }

    private fun hideAnimation() {
        showButtons(View.VISIBLE)
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.INVISIBLE
    }

    private fun showError() {
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.animationView).setAnimation(R.raw.error)
        findViewById<LottieAnimationView>(R.id.animationView).playAnimation()
        showButtons(View.INVISIBLE)
        findViewById<Button>(R.id.try_again).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorMessage).visibility = View.VISIBLE
    }

    private fun showLoading() {
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.animationView).setAnimation(R.raw.loading)
        findViewById<LottieAnimationView>(R.id.animationView).playAnimation()
        showButtons(View.INVISIBLE)
        findViewById<Button>(R.id.try_again).visibility = View.INVISIBLE
        findViewById<TextView>(R.id.errorMessage).visibility = View.INVISIBLE
    }

    private inner class RoomJoiner : AsyncTask<Void, Integer, ConnectionResult?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showLoading()
        }

        override fun onPostExecute(result: ConnectionResult?) {
            super.onPostExecute(result)
            if (result != null) {
                if (result.code == 200) {
                    val intent = Intent(context, LobbyActivity::class.java)
                    intent.putExtra("userName", playerName)
                    intent.putExtra("isHost", false)
                    intent.putExtra("lobbyDTO", result.responce)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(
                        this@JoinRoomActivity,
                        "Room doesn't exist",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 20)
                    toast.show()
                }
            } else {
                showError()
            }
        }

        override fun doInBackground(vararg params: Void?): ConnectionResult? {
            val userName = ChangeUsernameRequest()
            try {
                userName.username = playerName
                userName.token = TokenController.getToken(context)
                APIConnector.doPost("user/nickname", userName)

                val request = JoinLobbyRequest()
                request.token = TokenController.getToken(context)
                request.roomID = findViewById<EditText>(R.id.edit_text_room_code).text.toString()
                return APIConnector.doPost("lobby/join", request)
            } catch (e: Exception) {
                return null
            }
        }
    }
}
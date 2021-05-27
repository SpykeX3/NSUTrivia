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
import android.widget.Toast
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
        findViewById<EditText>(R.id.edit_text_room_code).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 6) {
                    findViewById<Button>(R.id.join_room).isFocusable = true
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_700))
                } else {
                    findViewById<Button>(R.id.join_room).isFocusable = false
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_200))
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
        findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
    }

    private fun joinRoom(roomCode: String){
        RoomJoiner().execute()
    }

    private inner class RoomJoiner: AsyncTask<Void, Integer, ConnectionResult>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.VISIBLE
        }

        override fun onPostExecute(result: ConnectionResult) {
            super.onPostExecute(result)
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
            if (result.code == 200) {
                val intent = Intent(context, LobbyActivity::class.java)
                intent.putExtra("userName", playerName)
                intent.putExtra("isHost", false)
                intent.putExtra("lobbyDTO", result.responce)
                startActivity(intent)
            }
            else{
                Log.d("AAAAA", result.code.toString() + result.message)
                val toast = Toast.makeText(this@JoinRoomActivity, "Something went wrong", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

        override fun doInBackground(vararg params: Void?): ConnectionResult {
            val userName = ChangeUsernameRequest()
            userName.username = playerName
            userName.token = TokenController.getToken(context)
            APIConnector.doPost("user/nickname", userName)

            val request = JoinLobbyRequest()
            request.token = TokenController.getToken(context)
            request.roomID = findViewById<EditText>(R.id.edit_text_room_code).text.toString()
            return APIConnector.doPost("lobby/join", request)
        }
    }
}
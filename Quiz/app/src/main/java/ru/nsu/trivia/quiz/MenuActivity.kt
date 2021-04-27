package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.airbnb.lottie.LottieAnimationView
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController

class MenuActivity : AppCompatActivity() {

    lateinit var playerName: EditText
    var context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        playerName = findViewById(R.id.edit_text_name)
        TokenGenerator().execute()

        findViewById<Button>(R.id.join_room).setOnClickListener { view ->
            val intent = Intent(this, JoinRoomActivity::class.java)
            intent.putExtra("userName", playerName.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.room_creation).setOnClickListener { view ->
            createRoom()
        }

        playerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    findViewById<Button>(R.id.join_room).isFocusable = true
                    findViewById<Button>(R.id.room_creation).isFocusable = true
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_700))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.purple_700))
                } else {
                    findViewById<Button>(R.id.join_room).isFocusable = false
                    findViewById<Button>(R.id.room_creation).isFocusable = false
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_200))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.purple_200))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun createRoom() {
        RoomCreator().execute()
    }

    private inner class RoomCreator: AsyncTask<Void, Integer, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val intent = Intent(context, LobbyActivity::class.java)
            intent.putExtra("userName", playerName.text.toString())
            intent.putExtra("isHost", true)
            intent.putExtra("lobbyDTO", result)
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
            startActivity(intent)
        }

        override fun doInBackground(vararg params: Void?): String {
            val userName = ChangeUsernameRequest()
            userName.username = playerName.text.toString()
            userName.token = TokenController.getToken(context)
            APIConnector.doPost("user/nickname", userName)

            val request = UsingTokenRequest()
            request.token = TokenController.getToken(context)
            return APIConnector.doPost("lobby/create", request)
        }
    }

    private inner class TokenGenerator : AsyncTask<Void, Integer, String>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            findViewById<LottieAnimationView>(R.id.animationView).visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg params: Void?): String? {
            if (TokenController.isTokenSaved(context)){
                Log.d("Token", TokenController.getToken(context))
                return TokenController.getToken(context)
            }
            val token = APIConnector.doPost("token/generate", null)
            TokenController.setToken(token, context)
            Log.d("Token", token)
            return token
        }
    }
}
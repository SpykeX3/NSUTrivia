package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.ConnectionResult
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
            if (playerName.text.isNotEmpty()) {
                val intent = Intent(this, JoinRoomActivity::class.java)
                intent.putExtra("userName", playerName.text.toString())
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.room_creation).setOnClickListener { view ->
            createRoom()
        }

        playerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.green_700))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.green_700))
                } else {
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.green_200))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.green_200))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun createRoom() {
        if (playerName.text.isNotEmpty()) {
            RoomCreator().execute()
        }
    }

    fun reconnect(view: View) {
        TokenGenerator().execute()
    }

    private inner class RoomCreator : AsyncTask<Void, Integer, ConnectionResult>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showLoading()
        }

        override fun onPostExecute(result: ConnectionResult) {
            super.onPostExecute(result)
            if (result.code == 200) {
                val intent = Intent(context, LobbyActivity::class.java)
                intent.putExtra("userName", playerName.text.toString())
                intent.putExtra("isHost", true)
                intent.putExtra("lobbyDTO", result.responce)
                startActivity(intent)
            } else {
                showError()
            }
        }

        override fun doInBackground(vararg params: Void?): ConnectionResult {
            val userName = ChangeUsernameRequest()
            userName.username = playerName.text.toString()
            userName.token = TokenController.getToken(context)
            APIConnector.doPost("user/nickname", userName)

            val request = UsingTokenRequest()
            request.token = TokenController.getToken(context)
            return APIConnector.doPost("lobby/create", request)
        }
    }

    private fun showButtons(value: Int) {
        findViewById<Button>(R.id.join_room).visibility = value
        findViewById<Button>(R.id.room_creation).visibility = value
    }

    private fun hideAnimation() {
        showButtons(View.VISIBLE)
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.INVISIBLE
    }

    private fun showError() {
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.errorAnimationView).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.loadingAnimationView).visibility = View.INVISIBLE
        findViewById<LottieAnimationView>(R.id.errorAnimationView).playAnimation()
        showButtons(View.INVISIBLE)
        findViewById<Button>(R.id.try_again).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorMessage).visibility = View.VISIBLE
    }

    private fun showLoading() {
        findViewById<ConstraintLayout>(R.id.animationLayout).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.errorAnimationView).visibility = View.INVISIBLE
        findViewById<LottieAnimationView>(R.id.loadingAnimationView).visibility = View.VISIBLE
        findViewById<LottieAnimationView>(R.id.loadingAnimationView).playAnimation()
        showButtons(View.INVISIBLE)
        findViewById<Button>(R.id.try_again).visibility = View.INVISIBLE
        findViewById<TextView>(R.id.errorMessage).visibility = View.INVISIBLE
    }

    private inner class TokenGenerator : AsyncTask<Void, Integer, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showLoading()
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                hideAnimation()
            } else {
                showError()
            }
        }

        override fun doInBackground(vararg params: Void?): Boolean {
            try {
                val response = APIConnector.doPost("token/generate", null)
                val token = response.responce.trim()
                TokenController.setToken(token, context)
                Log.d("Token", token)
                return true
            } catch (ignored: Exception) {
                return false
            }
        }
    }

}
package ru.nsu.trivia.quiz.gameFragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.LobbyState
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController
import java.util.concurrent.Executors

abstract class InRoomActivity : AppCompatActivity() {
    var leftRoom = false
    lateinit var lobby: LobbyDTO
    var isConnected = true
    var currRound = 0

    protected inner class RoomSubscriber : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            if (!leftRoom && result != null && (lobby.round > currRound || lobby.state != LobbyState.Playing)) {
                goToLobbyResult()
            } else {
                RoomGetter().executeOnExecutor(Executors.newFixedThreadPool(1))
            }
        }

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            try {
                val result =
                    APIConnector.doLongPoling(
                        "lobby/subscribe",
                        TokenController.getToken(this@InRoomActivity),
                        lobby.lastUpdated
                    )
                val objectMapper = ObjectMapper()
                lobby = objectMapper.readValue(result.responce)
                return lobby
            } catch (e: Exception) {
                isConnected = false
                return null
            }
        }
    }

    fun goToLobbyResult() {
        val intent = Intent(this, ResultsActivity::class.java)
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(lobby)
        intent.putExtra("LobbyDTO", json)
        startActivity(intent)
    }

    protected inner class RoomGetter : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            if (!leftRoom && result != null) {
                RoomSubscriber().executeOnExecutor(Executors.newFixedThreadPool(1))
                findViewById<LinearLayout>(R.id.error).visibility = View.INVISIBLE
                isConnected = true
            } else {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    RoomGetter().executeOnExecutor(Executors.newFixedThreadPool(1))
                }, 2000)
            }
        }


        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            try {
                val result =
                    APIConnector.doGet(
                        "lobby/subscribe",
                        TokenController.getToken(this@InRoomActivity)
                    )
                val objectMapper = ObjectMapper()
                lobby = objectMapper.readValue(result)
                return lobby
            } catch (e: Exception) {
                isConnected = false
                return null
            }
        }
    }
}
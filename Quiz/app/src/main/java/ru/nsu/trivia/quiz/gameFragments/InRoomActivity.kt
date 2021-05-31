package ru.nsu.trivia.quiz.gameFragments

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController

abstract class InRoomActivity : AppCompatActivity() {
    var leftRoom = false
    lateinit var lobby: LobbyDTO

    protected inner class RoomSubscriber : AsyncTask<Void, Int, LobbyDTO?>() {

        override fun onPostExecute(result: LobbyDTO?) {
            if (!leftRoom) {
                goToLobbyResult()
            }
        }

        override fun doInBackground(vararg params: Void?): LobbyDTO? {
            val result =
                APIConnector.doLongPoling(
                    "lobby/subscribe",
                    TokenController.getToken(this@InRoomActivity),
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

    fun goToLobbyResult() {
        val intent = Intent(this, ResultsActivity::class.java)
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(lobby)
        intent.putExtra("LobbyDTO", json)
        startActivity(intent)
    }
}
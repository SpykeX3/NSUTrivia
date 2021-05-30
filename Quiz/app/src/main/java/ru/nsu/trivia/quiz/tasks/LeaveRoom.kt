package ru.nsu.trivia.quiz.tasks

import android.content.Context
import android.os.AsyncTask
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest
import ru.nsu.trivia.quiz.clientTasks.APIConnector
import ru.nsu.trivia.quiz.clientTasks.TokenController

class LeaveRoom(var context: Context) : AsyncTask<Void, Int, LobbyDTO?>() {

    override fun doInBackground(vararg params: Void?): LobbyDTO? {
        val request = UsingTokenRequest()
        request.token = TokenController.getToken(context)
        APIConnector.doPost("lobby/leave", request)
        return null
    }
}
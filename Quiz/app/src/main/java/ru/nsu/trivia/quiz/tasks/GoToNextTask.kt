package ru.nsu.trivia.quiz.tasks

import android.os.AsyncTask
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.LobbyState
import ru.nsu.trivia.quiz.gameFragments.TaskActivity
import ru.nsu.trivia.quiz.gameFragments.TaskController

class GoToNextTask(val lobby: LobbyDTO, val context: TaskActivity) : AsyncTask<Int, Int, Int>() {
    override fun doInBackground(vararg params: Int?): Int {
        Thread.sleep(3000)
        return 0
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        if (lobby.state == LobbyState.Playing && !context.leftRoom) {
            val controller = TaskController(context)
            controller.goToTaskActivity(lobby)
        }
    }
}
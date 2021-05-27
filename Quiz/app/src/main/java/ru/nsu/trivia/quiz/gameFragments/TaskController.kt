package ru.nsu.trivia.quiz.gameFragments

import android.content.Context
import android.content.Intent
import com.fasterxml.jackson.databind.ObjectMapper
import ru.nsu.trivia.common.dto.model.LobbyDTO
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.common.dto.model.task.SetNearestValueTaskDTO

class TaskController(val context: Context) {

    fun goToTaskActivity(task: LobbyDTO) {
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(task)

        if (task.currentTask is SelectAnswerTaskDTO) {
            val intent = Intent(context, SelectAnswerTaskActivity::class.java)
            intent.putExtra("LobbyDTO", json)
            context.startActivity(intent)
        }

        if (task.currentTask is SetNearestValueTaskDTO){
             val intent = Intent(context, SetNearestAnswerActivity::class.java)
            intent.putExtra("LobbyDTO", json)
            context.startActivity(intent)
        }
    }
}
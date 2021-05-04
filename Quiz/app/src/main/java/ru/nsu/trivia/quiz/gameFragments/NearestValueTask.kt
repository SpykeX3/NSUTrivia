package ru.nsu.trivia.quiz.gameFragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTask
import ru.nsu.trivia.common.dto.model.task.SetNearestValueTask
import ru.nsu.trivia.quiz.QuizActivity
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.EmptyAdapter
import ru.nsu.trivia.quiz.adapters.VariantsRecyclerViewAdapter

class NearestValueTask : Fragment() {

    var isSet = false
    lateinit var task: SetNearestValueTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearest_value_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.max = 10
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (Math.abs(System.currentTimeMillis() - time) < 10 * 1000) {
                    progressBar?.progress = Math.abs(System.currentTimeMillis() - time).toInt() / 1000
                    handler.postDelayed(this, 1000)
                    println(-System.currentTimeMillis() + time)
                } else {
                    goToLobbyResult()
                }
            }
        })
    }

    fun goToLobbyResult(){
        (context as QuizActivity).showResults()
    }

    fun showQuestion(task: SetNearestValueTask) {
        if (view == null){
            isSet = true
            this.task = task
        } else {
            isSet = false
            view?.findViewById<TextView>(R.id.text_view_question)?.text = task.question
        }
    }
}
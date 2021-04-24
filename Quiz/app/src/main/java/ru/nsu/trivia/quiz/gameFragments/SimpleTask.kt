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
import ru.nsu.trivia.quiz.QuizActivity
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.adapters.EmptyAdapter
import ru.nsu.trivia.quiz.adapters.VariantsRecyclerViewAdapter


class SimpleTask : Fragment() {
    lateinit var task: SelectAnswerTask
    var isSet = false
    lateinit var adapter: VariantsRecyclerViewAdapter
    private var mLayoutManager = LinearLayoutManager(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_variants)

        mRecyclerView?.layoutManager = mLayoutManager
        if (!isSet) {
            mRecyclerView?.adapter = context?.let { EmptyAdapter(it) }
        }
        else{
            adapter = context?.let { VariantsRecyclerViewAdapter(it, task) }!!
            mRecyclerView?.adapter = adapter
            view.findViewById<TextView>(R.id.text_view_question)?.text = task.question
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.max = 10
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if (-System.currentTimeMillis() + time < 10 * 1000) {
                    progressBar?.progress = (System.currentTimeMillis() - time).toInt() / 1000
                    handler.postDelayed(this, 1000)
                } else {
                    goToLobbyResult()
                }
            }
        })
    }

    fun goToLobbyResult(){
        (context as QuizActivity).showResults()
    }

    fun showSimpleQuestion(task: SelectAnswerTask) {
        if (view == null){
            isSet = true
            this.task = task
        } else {
            isSet = false
            adapter = context?.let { VariantsRecyclerViewAdapter(it, task) }!!
            view?.findViewById<RecyclerView>(R.id.recycler_view_variants)?.adapter = adapter
            view?.findViewById<TextView>(R.id.text_view_question)?.text = task.question
        }
    }
}
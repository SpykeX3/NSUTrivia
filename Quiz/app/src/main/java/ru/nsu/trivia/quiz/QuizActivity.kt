package ru.nsu.trivia.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTask
import ru.nsu.trivia.common.dto.model.task.Task
import ru.nsu.trivia.quiz.gameFragments.SimpleTask
import ru.nsu.trivia.quiz.gameFragments.SimpleTaskResult
import java.util.*


class QuizActivity : AppCompatActivity() {
    lateinit var currTask: Task


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        currTask = getTask()
        showTask(currTask)
    }

    private fun goToLobbyResult() {

    }

    var q = 0
    fun getTask(): Task {
        q++
        val ans3 = ArrayList<String>()
        ans3.add("Собака")
        ans3.add("Кошка")
        ans3.add("Лошадь")
        ans3.add("Свинья")
        return SelectAnswerTask("canis lupus - название какого животного на латыни?", ans3, 0)
    }

    fun showTask(task: Task) {
        if (task is SelectAnswerTask) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_simple_task, SimpleTask::class.java, null)
                .commit()
            val currentFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.game_nav_fragment)?.childFragmentManager?.fragments?.get(0)
            if (currentFragment is SimpleTask) {
                currentFragment.showSimpleQuestion(task)
                return
            }
        }
    }

    fun showCorrect(view: View) {
        val task: SelectAnswerTask = currTask as SelectAnswerTask
        val fragment : Fragment? =
            supportFragmentManager.findFragmentById(R.id.game_nav_fragment)?.childFragmentManager?.fragments?.get(0)
        val id = fragment?.view?.findViewById<RecyclerView>(R.id.recycler_view_variants)?.getChildLayoutPosition(view)
        if (id != null) {
            if (fragment is SimpleTask) {
                fragment.adapter.showCorrect(id, task.correctVariantId)
                fragment.adapter.notifyDataSetChanged()
            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Todo: fix it. ask for task ect
                showResults()
            }
        }, 1000)
    }

    fun showResults(){
        supportFragmentManager.beginTransaction().replace(R.id.game_nav_fragment, SimpleTaskResult::class.java, null)
            .commit()
    }
}
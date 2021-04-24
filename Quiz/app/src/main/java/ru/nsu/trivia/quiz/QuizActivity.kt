package ru.nsu.trivia.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTask
import ru.nsu.trivia.quiz.adapters.VariantsRecyclerViewAdapter
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {
    final val QUIZ_TIME = 60
    lateinit var variances: List<SelectAnswerTask>
    private var currQ = 0
    private var mLayoutManager = LinearLayoutManager(this)
    private lateinit var adapter: VariantsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        variances = getQuestions()
        createRecyclerView()
        showQuestion()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = QUIZ_TIME
        val handler = Handler(Looper.getMainLooper())
        val time = System.currentTimeMillis()
        handler.post(object : Runnable {
            override fun run() {
                if ( - System.currentTimeMillis() + time < QUIZ_TIME*1000) {
                    progressBar.progress = (System.currentTimeMillis() - time).toInt()/1000
                    handler.postDelayed(this, 1000)
                }
                else{
                    goToLobbyResult()
                }
            }
        })
    }

    private fun goToLobbyResult(){

    }

    private fun showQuestion(){
        if (currQ < variances.size) {
            adapter = VariantsRecyclerViewAdapter(this, variances[currQ])
            findViewById<TextView>(R.id.text_view_question).text = variances[currQ].question
            adapter.notifyDataSetChanged()
            findViewById<RecyclerView>(R.id.recycler_view_variants).adapter = adapter
            currQ++;
        }
        else{
            goToLobbyResult()
        }
    }

    fun showCorrect(view: View){
        val id = findViewById<RecyclerView>(R.id.recycler_view_variants).getChildLayoutPosition(view)
        adapter.showCorrect(id, variances[currQ-1].correctVariantId)
        adapter.notifyDataSetChanged()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                showQuestion()
            }
        },1000 )
    }

    private fun createRecyclerView(){
        (mLayoutManager).orientation = LinearLayoutManager.VERTICAL
        val mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_variants)
        mRecyclerView.layoutManager = mLayoutManager
    }

    private fun getQuestions(): List<SelectAnswerTask> {
        //TODO: get list from the server
        val list = ArrayList<SelectAnswerTask>()
        val ans1 = ArrayList<String>()
        ans1.add("Белый")
        ans1.add("Черный")
        ans1.add("Синий")

        val ans2 = ArrayList<String>()
        ans2.add("2")
        ans2.add("3")
        ans2.add("4")
        ans2.add("5")

        val ans3 = ArrayList<String>()
        ans3.add("Собака")
        ans3.add("Кошка")
        ans3.add("Лошадь")
        ans3.add("Свинья")
        list.add(SelectAnswerTask("Какой цвет майки сейчас на Кате?", ans1, 2));
        list.add(SelectAnswerTask("2+2", ans2, 2))
        list.add(SelectAnswerTask("canis lupus - название какого животного на латыни?", ans3, 0))
        return list
    }
}
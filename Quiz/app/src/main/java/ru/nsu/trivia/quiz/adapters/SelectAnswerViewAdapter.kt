package ru.nsu.trivia.quiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO
import ru.nsu.trivia.quiz.R
import ru.nsu.trivia.quiz.gameFragments.SelectAnswerTaskActivity

class SelectAnswerViewAdapter(var context: Context, var responseList: SelectAnswerTaskDTO) :
    RecyclerView.Adapter<SelectAnswerViewAdapter.ViewHolder>() {
    private var showCorrect = false
    private var selectedItem = 0
    private var correct = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_quiz_variant, parent, false)
        return ViewHolder(view)
    }

    fun showCorrect(selectedItem: Int, correct: Int){
        if (!showCorrect) {
            this.selectedItem = selectedItem
            showCorrect = true
            this.correct = correct
        }
    }

    override fun onBindViewHolder(holder: SelectAnswerViewAdapter.ViewHolder, position: Int) {
        val request = responseList.variants[position]
        holder.textView.text = request
        if (showCorrect){
            if (correct == position){
                holder.textView.background = context.getDrawable(R.drawable.correct_ans_background)
            }
            if (correct != position && position == selectedItem){
                holder.textView.background = context.getDrawable(R.drawable.incorrect_ans_background)
            }
        }
        else{
            holder.textView.setOnClickListener { view ->
                (context as SelectAnswerTaskActivity).showCorrect(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return responseList.variants.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.ans)
    }


}
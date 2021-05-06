package ru.nsu.trivia.quiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.quiz.R

class PlayerRecyclerViewAdapter(var context: Context, var responseList: ArrayList<PlayerInLobby>) :
        RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = responseList[position]
        holder.textView.text = request.username
        holder.textView.setOnClickListener{ view ->
          //  (context as QuizActivity).showCorrect(view)
        }
        holder.imageView.visibility = if (request.isHost){
            View.VISIBLE
        } else{
            View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    fun setResponseListToNew(list: List<PlayerInLobby>){
        responseList.clear()
        responseList.addAll(list)
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.player_name)
        val imageView: ImageView = view.findViewById(R.id.ready_image)
    }
}
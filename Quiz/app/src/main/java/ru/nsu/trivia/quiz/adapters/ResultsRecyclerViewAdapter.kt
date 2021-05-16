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
import java.util.*
import kotlin.collections.ArrayList

class ResultsRecyclerViewAdapter(var context: Context, var responseList: ArrayList<PlayerInLobby>) :
    RecyclerView.Adapter<ResultsRecyclerViewAdapter.ViewHolder>() {
    var showWinner = false

    fun showWinner(){
        showWinner = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            responseList.sort()
            if (showWinner){
                holder.winner.visibility = View.VISIBLE
                holder.textView.visibility = View.INVISIBLE
            } else {
                holder.winner.visibility = View.INVISIBLE
                holder.textView.visibility = View.VISIBLE
            }
        }
        else{
            holder.winner.visibility = View.INVISIBLE
            holder.textView.visibility = View.VISIBLE
        }

        val request = responseList[position]
        holder.textView.text = request.username
        holder.winner.text = request.username
        holder.points.text = request.score.toString()
        holder.position.text = position.toString()
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    class ViewHolder internal constructor(var view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.player_name)
        val points: TextView = view.findViewById(R.id.points)
        val position:TextView = view.findViewById(R.id.winner)
        val winner: TextView = view.findViewById(R.id.player_name_winner)
    }
}
package ru.nsu.trivia.quiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.trivia.common.dto.model.PlayerInLobby
import ru.nsu.trivia.quiz.R

class EmptyAdapter(var context: Context) :
    RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 0
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
    }
}
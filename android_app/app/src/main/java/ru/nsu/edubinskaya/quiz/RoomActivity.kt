package ru.nsu.edubinskaya.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.nsu.edubinskaya.quiz.R
import ru.nsu.edubinskaya.quiz.adapters.PlayerInfo

class RoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
    }

    fun getRoomUsers(): List<PlayerInfo>{
        //TODO: get this list from the server
        val info = ArrayList<PlayerInfo>()
        info.add(PlayerInfo(intent.extras?.get("userName") as String, true))
        info.add(PlayerInfo("Vasyan97", false))
        info.add(PlayerInfo("Katushka2001", false))
        return info
    }

    fun fillRecyclerView(){
        
    }
}
package ru.nsu.trivia.quiz

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText


class MenuActivity : AppCompatActivity() {

    lateinit var playerName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        playerName = findViewById(R.id.edit_text_name)

        findViewById<Button>(R.id.join_room).setOnClickListener { view ->
            val intent = Intent(this, JoinRoomActivity::class.java)
            intent.putExtra("userName", playerName.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.room_creation).setOnClickListener { view ->
            createRoom()
        }

        playerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    findViewById<Button>(R.id.join_room).isFocusable = true
                    findViewById<Button>(R.id.room_creation).isFocusable = true
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_700))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.purple_700))
                } else {
                    findViewById<Button>(R.id.join_room).isFocusable = false
                    findViewById<Button>(R.id.room_creation).isFocusable = false
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_200))
                    findViewById<Button>(R.id.room_creation).setTextColor(resources.getColor(R.color.purple_200))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    fun createRoom(){
        //TODO: connect to server
        val intent = Intent(this, LobbyActivity::class.java)
        intent.putExtra("userName", playerName.text.toString())
        intent.putExtra("isHost", true);
        startActivity(intent)
    }
}
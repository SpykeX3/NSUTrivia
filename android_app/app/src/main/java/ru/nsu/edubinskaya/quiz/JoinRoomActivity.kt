package ru.nsu.edubinskaya.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class JoinRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)
        findViewById<EditText>(R.id.edit_text_room_code).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 4) {
                    findViewById<Button>(R.id.join_room).isFocusable = true
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_700))
                } else {
                    findViewById<Button>(R.id.join_room).isFocusable = false
                    findViewById<Button>(R.id.join_room).setTextColor(resources.getColor(R.color.purple_200))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        findViewById<Button>(R.id.join_room).setOnClickListener { view ->
           joinRoom(findViewById<EditText>(R.id.edit_text_room_code).text.toString())
        }
    }

    fun joinRoom(roomCode: String){
        //TODO: connect to server
        val intent = Intent(this, RoomActivity::class.java)
        intent.putExtra("userName", getIntent().extras?.getString("userName"))
        startActivity(intent)
    }
}
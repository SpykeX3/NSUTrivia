package ru.nsu.trivia.quiz.tasks

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import ru.nsu.trivia.quiz.MenuActivity
import ru.nsu.trivia.quiz.gameFragments.InRoomActivity
import java.util.concurrent.Executors

class AlertDialogCreator {
    fun createAlertDialog(context: InRoomActivity): AlertDialog {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        builder.setTitle("Do you want to leave room?")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            val intent =  Intent(context, MenuActivity::class.java)
            LeaveRoom(context).executeOnExecutor(Executors.newFixedThreadPool(1))
            context.leftRoom = true
            startActivity(context, intent, null)
        }
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        return builder.create()
    }
}
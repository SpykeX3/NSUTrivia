package ru.nsu.edubinskaya.quiz.adapters

class PlayerInfo(_name: String) {
    private val isHost = false
    private val name: String = _name

    fun isHost(): Boolean{
        return isHost
    }

    fun getName(): String{
        return name
    }
}
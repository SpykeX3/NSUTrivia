package ru.nsu.edubinskaya.quiz.adapters

class PlayerInfo(_name: String, _isHost: Boolean) {
    private val isHost = false
    private val name: String = _name

    fun isHost(): Boolean{
        return isHost
    }

    fun getName(): String{
        return name
    }
}
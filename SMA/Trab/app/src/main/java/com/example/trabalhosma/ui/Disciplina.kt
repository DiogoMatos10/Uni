package com.example.trabalhosma.ui

data class Disciplina(
    val nome: String = "",
    val ano: Int = 0,
    val concluida: Boolean = false,
    val nota: Int? = null,
    val sigla: String = "",
    val creditos: Int=0
) {
    constructor() : this("", 0, false,null,"",0)
}
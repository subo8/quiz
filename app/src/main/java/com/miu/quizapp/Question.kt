package com.miu.quizapp

data class Question (
    val id: Int,
    val statement: String,
    val options: List<QuestionOpt>
)
package com.miu.quizapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val userName = intent.getStringExtra(Constant.USER_NAME)
        val totalQues = intent.getIntExtra(Constant.TOTAL_QUESTION, 0)
        val rightAnswers = intent.getIntExtra(Constant.RIGHT_ANSWERS, 0)
        val totalScore: TextView = findViewById(R.id.totalScore)
        val yourScore: TextView = findViewById(R.id.yourScore)
        val wrongAnswer: TextView = findViewById(R.id.wrongAnswer)
        val congratsName: TextView = findViewById(R.id.congratsName)
        val btnTryAgain: Button = findViewById(R.id.btnTryAgain)

        totalScore.text = getString(R.string.totalQuestion, totalQues)
        yourScore.text = getString(R.string.totalScore, rightAnswers, totalQues)
        wrongAnswer.text = getString(R.string.wrong, totalQues-rightAnswers)
        congratsName.text = getString(R.string.congratsName, userName)

        btnTryAgain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
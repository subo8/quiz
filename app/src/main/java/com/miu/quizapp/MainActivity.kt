package com.miu.quizapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var startButton: Button? = null
    private var nameText: EditText? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    startButton = findViewById(R.id.startBtn)
    nameText = findViewById(R.id.nameInput)

        startButton?.setOnClickListener {
            if(nameText!!.text.isNotEmpty()) {
                val intent = Intent(this, QuestionActivity::class.java)
                intent.putExtra(Constant.USER_NAME, nameText!!.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please provide your name!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
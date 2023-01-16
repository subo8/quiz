package com.miu.quizapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

class QuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var progressBarLayout: LinearLayout? = null
    private var contentLayout: LinearLayout? = null
    private var sQuestion: TextView? = null
    private val options: MutableList<TextView> = ArrayList()
    private var progressBar: ProgressBar? = null
    private var progressText: TextView? = null
    private var buttonSubmit: Button? = null
    private val rQuestions: MutableList<Question> = ArrayList()
    private var rCurQuestion = 0
    private var rSelectedOption: ArrayList<Boolean> = arrayListOf(false, false, false, false, false, false)
    private var selectOption = true

    private var score: Double = 0.0
    private var rightAnswer: Int = 0
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        var username = intent.getStringExtra(Constant.USER_NAME)
        supportActionBar?.title = "Player: ${username}"

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        progressBarLayout = findViewById(R.id.progressBarLayout)
        contentLayout = findViewById(R.id.contentLayout)
        sQuestion = findViewById(R.id.question)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        options.add(findViewById(R.id.optionOne))
        options.add(findViewById(R.id.optionTwo))
        options.add(findViewById(R.id.optionThree))
        options.add(findViewById(R.id.optionFour))
        options.add(findViewById(R.id.optionFive))
        options.add(findViewById(R.id.optionSix))

        for (o in options) o.setOnClickListener(this)
        buttonSubmit?.setOnClickListener(this)

        val model: Model = ViewModelProvider(this)[Model::class.java]
        model.getQuestion().observe(this) {q -> loadQuestions(q)}
    }

    private fun loadQuestions(questions: List<Question>) {
        if (questions.isEmpty()) {
            Toast.makeText(this, "Connection Error!", Toast.LENGTH_SHORT).show()
            return
        }

        progressBarLayout!!.visibility = View.GONE
        contentLayout!!.visibility = View.VISIBLE

        rQuestions.addAll(questions)
        progressBar!!.max = rQuestions.size
        nextQuestion()
    }

    private fun selectOption(id: Int) {
        if (!selectOption) return
        val t: TextView = options[id]
        val question: Question = rQuestions[rCurQuestion - 1]
        selectSingleOption(t, id)

        updateSubmitBtn()
    }

    private fun selectSingleOption(t: TextView, id: Int) {
        if (rSelectedOption[id]) return

        for (i in options.indices) {
            if (i == id) continue
            rSelectedOption[i] = false
            options[i].setTextColor(Color.parseColor("#7A8089"))
            options[i].typeface = Typeface.DEFAULT
            options[i].background = ContextCompat.getDrawable(this@QuestionActivity, R.drawable.default_border)
        }

        t.setTextColor(ContextCompat.getColor(this@QuestionActivity, R.color.purple_500))
        t.setTypeface(t.typeface, Typeface.BOLD)
        t.background = ContextCompat.getDrawable(this@QuestionActivity, R.drawable.selected_border)
        rSelectedOption[id] = true
    }

    @SuppressLint("SetTextI18n")
    private fun nextQuestion() {
        rCurQuestion++
        val question = rQuestions[rCurQuestion - 1]
        resetSelection()
        sQuestion!!.text = question.statement

        for (i in question.options.indices) {
            options[i].visibility = View.VISIBLE
            options[i].text = question.options[i].statement
            options[i].background = ContextCompat.getDrawable(this@QuestionActivity, R.drawable.default_border)
            options[i].setTextColor(Color.parseColor("#7A8089"))
            options[i].typeface = Typeface.DEFAULT
        }

        for (i in question.options.size until options.size) {
            options[i].visibility = View.GONE
        }

        progressBar!!.progress = rCurQuestion
        progressText!!.text = "$rCurQuestion/${rQuestions.size}"

        updateSubmitBtn()
    }

    private fun enableSubmitBtn() {
        buttonSubmit!!.backgroundTintList = ContextCompat.getColorStateList(this@QuestionActivity, com.google.android.material.R.color.design_default_color_primary)
        buttonSubmit!!.setTextColor(ContextCompat.getColor(this@QuestionActivity, R.color.white))
        buttonSubmit!!.isEnabled = true
    }

    private fun disableSubmitBtn() {
        buttonSubmit!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#eeeeee"))
        buttonSubmit!!.setTextColor(Color.parseColor("#7A8089"))
        buttonSubmit!!.isEnabled = false
    }

    private fun updateSubmitBtn() {
        if (countOptionsSelected() > 0) enableSubmitBtn()
        else disableSubmitBtn()
    }

    private fun countOptionsSelected(): Int {
        var count = 0

        for (i in rSelectedOption) {
            if (i) count++
        }

        return count
    }

    private fun resetSelection() {
        for (i in rSelectedOption.indices) {
            rSelectedOption[i] = false
        }
    }

    private fun showResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(Constant.SCORE, score)
        intent.putExtra(Constant.TOTAL_QUESTION, rQuestions.size)
        intent.putExtra(Constant.RIGHT_ANSWERS, rightAnswer)
        intent.putExtra(Constant.USER_NAME, userName)
        startActivity(intent)
        finish()
    }

    private fun revealAnswers() {
        val question = rQuestions[rCurQuestion - 1]
        var totalCorrectAnswers = 0
        var selectedCorrectAnswers = 0
        for (i in question.options.indices) {
            if (question.options[i].isCorrect) totalCorrectAnswers += 1
            if (rSelectedOption[i]) {
                val style = if (question.options[i].isCorrect) R.drawable.correct_border else R.drawable.incorrect_border
                if (question.options[i].isCorrect) {
                    selectedCorrectAnswers += 1
                }
                applyAnswer(options[i], style)
            } else if (question.options[i].isCorrect) {
                applyAnswer(options[i], R.drawable.correct_not_selected)
            }
        }
        score += selectedCorrectAnswers / totalCorrectAnswers.toDouble()
        if (totalCorrectAnswers == selectedCorrectAnswers)
            rightAnswer++
        resetSelection()
    }

    private fun applyAnswer(tv: TextView, style: Int) {
        if (style != R.drawable.correct_not_selected)
            tv.setTextColor(ContextCompat.getColor(this@QuestionActivity, R.color.white))
        tv.background = ContextCompat.getDrawable(this@QuestionActivity, style)
    }

    @SuppressLint("SetTextI18n")
    private fun onSubmit() {
        if (countOptionsSelected() == 0) {
            selectOption = true
            if (rCurQuestion >= rQuestions.size) {
                showResult()
            } else {
                buttonSubmit!!.text = "SUBMIT"
                nextQuestion()
            }
        } else {
            revealAnswers()
            buttonSubmit!!.text = if (rCurQuestion >= rQuestions.size) "SEE THE RESULT" else "NEXT QUESTION"
            selectOption = false
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.optionOne -> selectOption(0)
            R.id.optionTwo -> selectOption(1)
            R.id.optionThree -> selectOption(2)
            R.id.optionFour -> selectOption(3)
            R.id.optionFive -> selectOption(4)
            R.id.optionSix -> selectOption(5)
            R.id.buttonSubmit -> onSubmit()
        }
    }
}
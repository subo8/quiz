package com.miu.quizapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class Model: ViewModel() {
    private val questions: MutableLiveData<List<Question>> = MutableLiveData<List<Question>>()

    private fun loadQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            val questions: MutableList<Question> = ArrayList()
            var conn: HttpURLConnection? = null

            try {
                val url = URL("https://quizapi.io/api/v1/questions?apiKey=${Constant.API_KEY}&limit=15&category=code&tags=javascript&multiple=false")
                conn = url.openConnection() as HttpURLConnection

                val status: Int = conn.responseCode

                if(status == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        while(reader.readLine().also { line = it } !=null) {
                            stringBuilder.append(line + '\n')
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    questions.addAll(parseResult(stringBuilder.toString()))
                } else {
                    Log.e("Model", "Connection error!: ${conn.responseMessage}")
                }
            } catch (e: SocketTimeoutException) {
                Log.e("Model", e.toString())
            } catch (e: Exception) {
                Log.e("Model", e.toString())
            } finally {
                conn?.disconnect()
            }

            withContext(Dispatchers.Main) {this@Model.questions.value = questions}
        }
    }

    fun getQuestion(): LiveData<List<Question>> {
        return questions
    }

    data class AnswerNode(
        val answer_a: String? = null,
        val answer_b: String? = null,
        val answer_c: String? = null,
        val answer_d: String? = null,
        val answer_e: String? = null,
        val answer_f: String? = null
    )

    data class CorrectAnswerNode(
        val answer_a_correct: Boolean,
        val answer_b_correct: Boolean,
        val answer_c_correct: Boolean,
        val answer_d_correct: Boolean,
        val answer_e_correct: Boolean,
        val answer_f_correct: Boolean,
    )

    data class QuestionNode(
        val id: String,
        val question: String,
        val description: String,
        val answers: AnswerNode,
        val correct_answers: CorrectAnswerNode,
        val difficulty: Boolean
    )

    private fun parseResult(str: String): List<Question> {
        val questionList: List<QuestionNode> = Gson().fromJson(str, Array<QuestionNode>::class.java).toList()
        val questions = ArrayList<Question>()

        for (q in questionList) {
            val options = ArrayList<QuestionOpt>()
            q.answers.answer_a?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_a_correct))
            }

            q.answers.answer_b?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_b_correct))
            }

            q.answers.answer_c?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_c_correct))
            }

            q.answers.answer_d?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_d_correct))
            }

            q.answers.answer_e?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_e_correct))
            }

            q.answers.answer_f?.let {
                options.add(QuestionOpt(it, q.correct_answers.answer_f_correct))
            }

            questions.add(Question(q.id.toInt(), q.question, options))
        }

        return questions
    }

    init {
        loadQuestion()
    }
}
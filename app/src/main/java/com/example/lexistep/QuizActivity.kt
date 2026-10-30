package com.example.lexistep

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.BufferedReader
import java.io.InputStreamReader

// Экран 4: изучение слов
class QuizActivity : AppCompatActivity() {

    private val questionList = mutableListOf<Question>()
    private var currentIndex = 0
    private var score = 0
    private var answered = false

    private var language = ""
    private var topic = ""

    private lateinit var tvProgressCount: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var tvQuestionWord: TextView
    private lateinit var bottomPanel: View
    private lateinit var tvFeedback: TextView

    private lateinit var variants: List<View>
    private lateinit var variantTexts: List<TextView>
    private lateinit var variantIcons: List<ImageView>

    private val fontMedium by lazy { ResourcesCompat.getFont(this, R.font.inter_medium) }
    private val fontBold by lazy { ResourcesCompat.getFont(this, R.font.inter_bold) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        findViewById<View>(R.id.main).applySystemBarsPadding()

        language = intent.getStringExtra(Extras.LANGUAGE) ?: ""
        topic = intent.getStringExtra(Extras.TOPIC) ?: ""

        findViewById<TextView>(R.id.tvTopic).text = topic
        findViewById<TextView>(R.id.tvLanguageLabel).text = language
        tvProgressCount = findViewById(R.id.tvProgressCount)
        progressBar = findViewById(R.id.progressBar)
        tvQuestionWord = findViewById(R.id.tvQuestionWord)
        bottomPanel = findViewById(R.id.bottomPanel)
        tvFeedback = findViewById(R.id.tvFeedback)

        variants = listOf(
            findViewById(R.id.variant1), findViewById(R.id.variant2),
            findViewById(R.id.variant3), findViewById(R.id.variant4)
        )
        variantTexts = listOf(
            findViewById(R.id.tvVariant1), findViewById(R.id.tvVariant2),
            findViewById(R.id.tvVariant3), findViewById(R.id.tvVariant4)
        )
        variantIcons = listOf(
            findViewById(R.id.ivVariant1), findViewById(R.id.ivVariant2),
            findViewById(R.id.ivVariant3), findViewById(R.id.ivVariant4)
        )

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnClose).setOnClickListener { goHome() }
        findViewById<View>(R.id.btnNext).setOnClickListener { nextQuestion() }

        variants.forEachIndexed { index, row ->
            row.setOnClickListener { checkAnswer(index) }
        }

        loadQuestionsFromAssets()

        if (questionList.isEmpty()) {
            Toast.makeText(this, "Нет слов для выбранной темы", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        progressBar.max = questionList.size
        showQuestion()
    }

    private fun loadQuestionsFromAssets() {
        val pairs = mutableListOf<Pair<String, String>>()

        BufferedReader(InputStreamReader(assets.open("words.txt"), "UTF-8")).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                val parts = line.split("|")
                // оставляем только строки выбранного языка и темы
                if (parts.size == 4 && parts[0].trim() == language && parts[1].trim() == topic) {
                    pairs.add(Pair(parts[2].trim(), parts[3].trim()))
                }
                line = reader.readLine()
            }
        }

        pairs.shuffle()
        val allTranslations = pairs.map { it.second }

        for ((word, correct) in pairs.take(QUESTIONS_PER_ROUND)) {
            val wrong = allTranslations.filter { it != correct }.shuffled().take(3)
            val options = (wrong + correct).shuffled()   // перемешиваем, чтобы верный был на случайном месте
            questionList.add(Question(word, options, options.indexOf(correct)))
        }
    }


    private fun showQuestion() {
        answered = false
        val q = questionList[currentIndex]

        tvProgressCount.text = "${currentIndex + 1}/${questionList.size}"
        progressBar.setProgressCompat(currentIndex + 1, true)
        tvQuestionWord.text = q.questionText

        variants.forEachIndexed { i, row ->
            row.setBackgroundResource(R.drawable.shape_rounded_variants)
            variantTexts[i].text = q.options[i]
            variantTexts[i].setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            variantTexts[i].typeface = fontMedium
            variantIcons[i].visibility = View.GONE
        }
        bottomPanel.visibility = View.GONE
    }

    private fun checkAnswer(selected: Int) {
        if (answered) return
        answered = true

        val q = questionList[currentIndex]
        highlight(q.correctIndex, R.drawable.bg_option_correct, R.drawable.ic_check_white)

        if (selected == q.correctIndex) {
            score++
            tvFeedback.text = getString(R.string.correct)
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        } else {
            highlight(selected, R.drawable.bg_option_wrong, R.drawable.ic_x_white)
            tvFeedback.text = getString(R.string.wrong, q.options[q.correctIndex])
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.error))
        }
        bottomPanel.visibility = View.VISIBLE
    }

    private fun highlight(index: Int, backgroundRes: Int, iconRes: Int) {
        variants[index].setBackgroundResource(backgroundRes)
        variantTexts[index].setTextColor(Color.WHITE)
        variantTexts[index].typeface = fontBold
        variantIcons[index].setImageResource(iconRes)
        variantIcons[index].visibility = View.VISIBLE
    }

    private fun nextQuestion() {
        currentIndex++
        if (currentIndex >= questionList.size) {
            openResult()
        } else {
            showQuestion()
        }
    }

    private fun openResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(Extras.SCORE, score)
        intent.putExtra(Extras.TOTAL, questionList.size)
        intent.putExtra(Extras.LANGUAGE, language)
        intent.putExtra(Extras.TOPIC, topic)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val QUESTIONS_PER_ROUND = 10
    }
}

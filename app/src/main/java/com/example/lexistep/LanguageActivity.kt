package com.example.lexistep

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Экран 3: выбор языка и темы
class LanguageActivity : AppCompatActivity() {

    private var selectedLanguage: String? = null
    private var selectedTopic: String? = null

    private lateinit var languageRows: List<View>
    private lateinit var topicCards: List<View>
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language)
        findViewById<View>(R.id.main).applySystemBarsPadding()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        btnContinue = findViewById(R.id.btnContinue)

        val languageNames = listOf(
            getString(R.string.lang_en), getString(R.string.lang_es),
            getString(R.string.lang_de), getString(R.string.lang_fr)
        )
        val topicNames = listOf(
            getString(R.string.topic_basic), getString(R.string.topic_airport),
            getString(R.string.topic_transport)
        )

        languageRows = listOf(
            findViewById(R.id.rowEn), findViewById(R.id.rowEs),
            findViewById(R.id.rowDe), findViewById(R.id.rowFr)
        )
        topicCards = listOf(
            findViewById(R.id.cardBasic), findViewById(R.id.cardAirport),
            findViewById(R.id.cardTransport)
        )

        languageRows.forEachIndexed { index, row ->
            row.setOnClickListener {
                selectedLanguage = languageNames[index]
                languageRows.forEachIndexed { i, r -> r.isSelected = (i == index) }
                updateContinueButton()
            }
        }
        topicCards.forEachIndexed { index, card ->
            card.setOnClickListener {
                selectedTopic = topicNames[index]
                topicCards.forEachIndexed { i, c -> c.isSelected = (i == index) }
                updateContinueButton()
            }
        }

        btnContinue.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra(Extras.LANGUAGE, selectedLanguage)
            intent.putExtra(Extras.TOPIC, selectedTopic)
            startActivity(intent)
        }
    }

    private fun updateContinueButton() {
        btnContinue.isEnabled = selectedLanguage != null && selectedTopic != null
    }
}

package com.example.lexistep

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        findViewById<View>(R.id.main).applySystemBarsPadding()

        val score = intent.getIntExtra(Extras.SCORE, 0)
        val total = intent.getIntExtra(Extras.TOTAL, 0)
        val language = intent.getStringExtra(Extras.LANGUAGE) ?: ""
        val topic = intent.getStringExtra(Extras.TOPIC) ?: ""

        val percent = if (total == 0) 0 else score * 100 / total

        findViewById<TextView>(R.id.tvPercent).text = "$percent%"
        findViewById<CircularProgressIndicator>(R.id.progressCircle).setProgressCompat(percent, true)
        findViewById<TextView>(R.id.tvResultDetails).text =
            getString(R.string.result_details, score, total)
        findViewById<TextView>(R.id.tvResultTitle).setText(
            when {
                percent >= 80 -> R.string.result_great
                percent >= 50 -> R.string.result_good
                else -> R.string.result_try_again
            }
        )

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnHome).setOnClickListener { goHome() }

        findViewById<View>(R.id.btnRetry).setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra(Extras.LANGUAGE, language)
            intent.putExtra(Extras.TOPIC, topic)
            startActivity(intent)
            finish()
        }
    }
}

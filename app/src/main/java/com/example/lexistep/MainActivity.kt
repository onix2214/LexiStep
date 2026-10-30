package com.example.lexistep

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Экран 1: главная (логотип + кнопки «Начать» и «Об авторах»)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<android.view.View>(R.id.main).applySystemBarsPadding()

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnAuthors = findViewById<Button>(R.id.btnAuthors)

        btnStart.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
        btnAuthors.setOnClickListener {
            startActivity(Intent(this, AuthorsActivity::class.java))
        }
    }
}

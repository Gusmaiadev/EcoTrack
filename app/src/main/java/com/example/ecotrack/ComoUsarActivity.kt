package com.example.ecotrack

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class ComoUsarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_como_usar)

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            finish()
        }

    }
}
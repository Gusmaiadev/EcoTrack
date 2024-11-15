package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DicaGeladeiraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dica_geladeira)

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.proxDica).setOnClickListener {
            val intent = Intent(this, DicaTelevisaoActivity::class.java)
            startActivity(intent)
        }
    }
}
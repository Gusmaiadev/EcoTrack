package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class DicaChuveiroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dica_chuveiro)

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            val intent = Intent(this, DicasActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.proxDica).setOnClickListener {
            val intent = Intent(this, DicaFogaoActivity::class.java)
            startActivity(intent)
        }

    }
}
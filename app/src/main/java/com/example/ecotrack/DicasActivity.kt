package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DicasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dicas)

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnGeladeira).setOnClickListener {
            val intent = Intent(this, DicaGeladeiraActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnTelevisao).setOnClickListener {
            val intent = Intent(this, DicaTelevisaoActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnMicroondas).setOnClickListener {
            val intent = Intent(this, DicaMicroondasActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnChuveiro).setOnClickListener {
            val intent = Intent(this, DicaChuveiroActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnLuz).setOnClickListener {
            val intent = Intent(this, DicaLuzActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnFogao).setOnClickListener {
            val intent = Intent(this, DicaFogaoActivity::class.java)
            startActivity(intent)
        }
    }
}
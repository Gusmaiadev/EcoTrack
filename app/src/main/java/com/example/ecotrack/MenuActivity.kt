package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        findViewById<CardView>(R.id.cardConsumo).setOnClickListener {
            val intent = Intent(this, MeuConsumoActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnCadastrar).setOnClickListener {
            val intent = Intent(this, CadastroEletrodomesticosActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnRelatorios).setOnClickListener {
            val intent = Intent(this, RelatorioConsumoActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnComoUsar).setOnClickListener {
            val intent = Intent(this, ComoUsarActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnDicas).setOnClickListener {
            val intent = Intent(this, DicasActivity::class.java)
            startActivity(intent)
        }
    }
}
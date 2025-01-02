package com.example.proyecto911


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnPolice = findViewById<Button>(R.id.btnPolice)
        val btnHealth = findViewById<Button>(R.id.btnHealth)
        val btnTraffic = findViewById<Button>(R.id.btnTraffic)
        val btnFirefighters = findViewById<Button>(R.id.btnFirefighters)

        btnPolice.setOnClickListener {
            val intent = Intent(this, PoliceActivity::class.java)
            startActivity(intent)
        }
        btnTraffic.setOnClickListener {
            val intent = Intent(this, TransitoActivity::class.java)
            startActivity(intent)
        }
        btnHealth.setOnClickListener {
            val intent = Intent(this, SaludActivity::class.java)
            startActivity(intent)
        }
        btnFirefighters.setOnClickListener {
            val intent = Intent(this, BomberoActivity::class.java)
            startActivity(intent)
        }


    }
}
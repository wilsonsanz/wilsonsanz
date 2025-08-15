package com.example.segura_control

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.segura.Activity.IngresoActivity
import com.segura.Activity.RutasActivity

class Administrador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrador)


        val btncitapro = findViewById<Button>(R.id.btnCancelarCita)
        val btnVerCita = findViewById<Button>(R.id.btnVerCita)




        btncitapro.setOnClickListener {
            val intent = Intent(this, IngresoActivity::class.java)
            startActivity(intent)
        }
        btnVerCita.setOnClickListener {
            val intent = Intent(this, RutasActivity::class.java)
            startActivity(intent)
        }



    }
}
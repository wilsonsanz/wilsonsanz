package com.example.proyecto911

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SaludActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saludmenuactivity)

        val btnIntoxicado = findViewById<Button>(R.id.btnIntoxicado)
        val btnAtaqueCardiaco = findViewById<Button>(R.id.btnAtaqueCardiaco)
        val btnConducteEbrio = findViewById<Button>(R.id.btnHeridabala)
        val btnCaida = findViewById<Button>(R.id.btnCaida)
        val btnEmbarazo = findViewById<Button>(R.id.btnEmbarazo)
        val btnProblemaRespiratorio = findViewById<Button>(R.id.btnProblemaRespiratorio)



        btnIntoxicado.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnAtaqueCardiaco.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnConducteEbrio.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnCaida.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnEmbarazo.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnProblemaRespiratorio.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }


    }

    private fun mostrarMensaje(tipo: String) {
        Toast.makeText(this, "$tipo Gracias", Toast.LENGTH_SHORT).show()
    }
}
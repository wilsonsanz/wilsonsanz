package com.example.proyecto911

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PoliceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.policiamenuactivity)

        val btnViolencia = findViewById<Button>(R.id.btnViolencia)
        val btnRobo = findViewById<Button>(R.id.btnRobo)
        val btnDisparos = findViewById<Button>(R.id.btnDisparos)
        val btnPersonaArmada = findViewById<Button>(R.id.btnPersonaArmada)
        val btnSecuestro = findViewById<Button>(R.id.btnSecuestro)
        val btnRoboDomicilio = findViewById<Button>(R.id.btnRobo_domicilio)
        val btnPresuntoDelincuente = findViewById<Button>(R.id.btnPresunto_delincuente)
        val btnSicariato = findViewById<Button>(R.id.btnSicariato)
        val btnPersonaDesaparecida = findViewById<Button>(R.id.btnDesaparecida)
        val btnConsumoDroga = findViewById<Button>(R.id.btnConsumo_droga)
        val btnLibadores = findViewById<Button>(R.id.btnLibadores)


        btnViolencia.setOnClickListener { mostrarMensaje("Violencia") }
        btnRobo.setOnClickListener { mostrarMensaje("Robo") }
        btnDisparos.setOnClickListener { mostrarMensaje("Disparos") }
        btnPersonaArmada.setOnClickListener { mostrarMensaje("Persona Armada") }
        btnSecuestro.setOnClickListener { mostrarMensaje("Secuestro") }
        btnRoboDomicilio.setOnClickListener { mostrarMensaje("Robo a Domicilio") }
        btnPresuntoDelincuente.setOnClickListener { mostrarMensaje("Presunto Delincuente") }
        btnSicariato.setOnClickListener { mostrarMensaje("Sicariato") }
        btnPersonaDesaparecida.setOnClickListener { mostrarMensaje("Persona Desaparecida") }
        btnConsumoDroga.setOnClickListener { mostrarMensaje("Consumo de Droga") }
        btnLibadores.setOnClickListener { mostrarMensaje("Libadores") }
    }

    private fun mostrarMensaje(tipo: String) {
        Toast.makeText(this, "$tipo Seleccionado", Toast.LENGTH_SHORT).show()
    }
}
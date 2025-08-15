package com.segura.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.R

class novedades_activity : AppCompatActivity() {

    // Variable para almacenar el número de registro que se recibirá
    private var numeroRegistro: String? = null
    // **NUEVA VARIABLE: Para almacenar el nombre del conductor que se recibirá**
    private var nombreConductor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.novedades_menu)

        // 1. Recibir el numeroRegistro del Intent que viene de MenuActivity
        numeroRegistro = intent.getStringExtra("numeroRegistro")
        // **NUEVO: Recibir el nombre del conductor del Intent que viene de MenuActivity**
        nombreConductor = intent.getStringExtra("nombreConductor")

        val btnpuntomartillo = findViewById<Button>(R.id.btnPuntomartillo)
        val btnlocalseguro = findViewById<Button>(R.id.btnLocalSeguro)
        val btncolaboracion = findViewById<Button>(R.id.btnColaboracion)
        val btnnovedades_circulacion = findViewById<Button>(R.id.btnNovedades_circulacion)

        btnpuntomartillo.setOnClickListener { elegirnombreboton("Punto Martillo") }
        btnlocalseguro.setOnClickListener { elegirnombreboton("Local Seguro") }
        btncolaboracion.setOnClickListener { elegirnombreboton("Colaboracion") }
        btnnovedades_circulacion.setOnClickListener { elegirnombreboton("Novedad en Circulacion") }
    }

    private fun elegirnombreboton(tipo: String) {
        val intent = Intent(this, RegistroNovedades::class.java)
        intent.putExtra("especialidad", tipo)
        // 2. Pasar el numeroRegistro recibido a RegistroNovedades
        intent.putExtra("numeroRegistro", numeroRegistro)
        // **NUEVO: Pasar el nombre del conductor recibido a RegistroNovedades**
        intent.putExtra("nombreConductor", nombreConductor)
        startActivity(intent)
    }
}
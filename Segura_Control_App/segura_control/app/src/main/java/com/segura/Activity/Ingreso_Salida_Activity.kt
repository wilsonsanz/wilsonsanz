package com.segura.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.R

class Ingreso_Salida_Activity : AppCompatActivity() {

    // Variable para almacenar el número de registro que se recibirá
    private var numeroRegistro: String? = null
    // Nueva variable para almacenar el nombre del conductor
    private var nombreConductor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso_salida)

        // PASO 1: RECUPERAR EL NÚMERO DEL INTENT
        numeroRegistro = intent.getStringExtra("numeroRegistro")
        // **NUEVO: RECUPERAR EL NOMBRE DEL CONDUCTOR DEL INTENT**
        nombreConductor = intent.getStringExtra("nombreConductor")


        val btningre = findViewById<Button>(R.id.btningre)
        val btnsali = findViewById<Button>(R.id.btnsali)
        val btncombustible = findViewById<Button>(R.id.btncombustible)

        btningre.setOnClickListener {
            val intent = Intent(this, IngresoActivity::class.java)
            // PASO 2: PASAR EL numeroRegistro AL NUEVO INTENT
            intent.putExtra("numeroRegistro", numeroRegistro)
            // **NUEVO: PASAR EL nombreConductor AL NUEVO INTENT**
            intent.putExtra("nombreConductor", nombreConductor)
            startActivity(intent)
        }

        btnsali.setOnClickListener {
            val intent = Intent(this, SalidaActivity::class.java)
            // PASO 2: PASAR EL numeroRegistro AL NUEVO INTENT
            intent.putExtra("numeroRegistro", numeroRegistro)
            // **NUEVO: PASAR EL nombreConductor AL NUEVO INTENT**
            intent.putExtra("nombreConductor", nombreConductor)
            startActivity(intent)
        }

        btncombustible.setOnClickListener {
            val intent = Intent(this, CombustibleActivity::class.java)
            // PASO 2: PASAR EL numeroRegistro AL NUEVO INTENT
            intent.putExtra("numeroRegistro", numeroRegistro)
            // **NUEVO: PASAR EL nombreConductor AL NUEVO INTENT (Si CombustibleActivity lo necesita)**
            intent.putExtra("nombreConductor", nombreConductor) // Puedes quitar esta línea si CombustibleActivity no necesita el nombre del conductor
            startActivity(intent)
        }
    }
}
package com.example.adivinar_zodiacal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Elementos de la interfaz
        val diaNacimiento = findViewById<EditText>(R.id.diaNacimiento)
        val mesNacimiento = findViewById<EditText>(R.id.mesNacimiento)
        val botonCalcular = findViewById<Button>(R.id.botonCalcular)
        val mensajeZodiacal = findViewById<TextView>(R.id.mensajeZodiacal)

        // Acción al presionar el botón
        botonCalcular.setOnClickListener {
            // Obtener los valores de entrada
            val dia = diaNacimiento.text.toString().toIntOrNull()
            val mes = mesNacimiento.text.toString().toLowerCase()

            // Validar entradas
            if (dia != null && mes.isNotEmpty()) {
                val signo = calcularSignoZodiacal(dia, mes)
                if (signo != null) {
                    mensajeZodiacal.text = "Tu signo es $signo"
                } else {
                    Toast.makeText(this, "Fecha no válida", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa una fecha válida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para calcular el signo zodiacal
    fun calcularSignoZodiacal(dia: Int, mes: String): String? {
        return when (mes) {
            "enero" -> if (dia <= 19) "Capricornio" else "Acuario"
            "febrero" -> if (dia <= 18) "Acuario" else "Piscis"
            "marzo" -> if (dia <= 20) "Piscis" else "Aries"
            "abril" -> if (dia <= 19) "Aries" else "Tauro"
            "mayo" -> if (dia <= 20) "Tauro" else "Géminis"
            "junio" -> if (dia <= 20) "Géminis" else "Cáncer"
            "julio" -> if (dia <= 22) "Cáncer" else "Leo"
            "agosto" -> if (dia <= 22) "Leo" else "Virgo"
            "septiembre" -> if (dia <= 22) "Virgo" else "Libra"
            "octubre" -> if (dia <= 22) "Libra" else "Escorpio"
            "noviembre" -> if (dia <= 21) "Escorpio" else "Sagitario"
            "diciembre" -> if (dia <= 21) "Sagitario" else "Capricornio"
            else -> null
        }
    }
}

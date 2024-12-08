package com.example.validar_contrase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contraseñaInput = findViewById<EditText>(R.id.contraseñaInput)
        val botonVerificar = findViewById<Button>(R.id.botonVerificar)
        val resultado = findViewById<TextView>(R.id.resultado)

        // Contraseña fija
        val contraseñaCorrecta = "12345"

        botonVerificar.setOnClickListener {
            val contraseñaIngresada = contraseñaInput.text.toString()

            if (contraseñaIngresada == contraseñaCorrecta) {
                resultado.text = "Acceso concedido"
            } else {
                resultado.text = "Contraseña incorrecta"
            }
        }
    }
}

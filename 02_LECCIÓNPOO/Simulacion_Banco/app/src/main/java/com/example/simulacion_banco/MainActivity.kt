package com.example.simulacion_banco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout de la actividad que contiene la interfaz de usuario
        setContentView(R.layout.activity_main)

        // Vincula los elementos de la interfaz (EditText para el usuario y la contraseña, y el botón de login)
        val username = findViewById<EditText>(R.id.editUsername)
        val password = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        // Configura un listener para el botón de login
        loginButton.setOnClickListener {
            // Verifica si el usuario y la contraseña son correctos
            if (username.text.toString() == "wilsonsanz" && password.text.toString() == "1234") {
                // Si la autenticación es exitosa, lanza la actividad DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent) // Inicia la nueva actividad
                finish() // Finaliza la actividad actual (MainActivity) para que no sea accesible con el botón "Atrás"
            } else {
                // Si la autenticación falla, muestra un mensaje de error con un Toast
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

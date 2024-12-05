package com.example.control_acceso

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var intentosRestantes = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Elementos de la interfaz
        val usuarioInput = findViewById<EditText>(R.id.usuarioInput)
        val contraseñaInput = findViewById<EditText>(R.id.contraseñaInput)
        val botonLogin = findViewById<Button>(R.id.botonLogin)
        val mensajeLogin = findViewById<TextView>(R.id.mensajeLogin)

        // Usuario y contraseña correctos
        val usuarioCorrecto = "admin"
        val contraseñaCorrecta = "1234"

        // Acción al presionar el botón
        botonLogin.setOnClickListener {
            // Obtener los valores de entrada
            val usuario = usuarioInput.text.toString()
            val contraseña = contraseñaInput.text.toString()

            // Validar el usuario y la contraseña
            if (usuario == usuarioCorrecto && contraseña == contraseñaCorrecta) {
                mensajeLogin.text = "Bienvenido, $usuario."
                Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()
            } else {
                intentosRestantes-- // Reducir los intentos restantes
                if (intentosRestantes > 0) {
                    mensajeLogin.text = "Usuario o contraseña incorrectos. Intentos restantes: $intentosRestantes"
                } else {
                    mensajeLogin.text = "Acceso bloqueado. Has superado los intentos permitidos."
                    botonLogin.isEnabled = false // Deshabilitar el botón de login
                    Toast.makeText(this, "Acceso bloqueado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

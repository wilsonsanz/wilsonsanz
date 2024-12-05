package com.example.paroimpar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var numeroUsuario: EditText
    private lateinit var calcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las vistas
        numeroUsuario = findViewById(R.id.numeroUsuario)
        calcular = findViewById(R.id.btnCalcular)

        // Configura el botón para calcular si el número es par o impar
        calcular.setOnClickListener {
            verificarParImpar()
        }
    }

    private fun verificarParImpar() {
        // Obtiene el texto ingresado y lo convierte en número
        val numeroTexto = numeroUsuario.text.toString()
        if (numeroTexto.isNotEmpty()) {
            val numero = numeroTexto.toIntOrNull()
            if (numero != null) {
                // Determina si el número es par o impar
                val resultado = if (numero % 2 == 0) {
                    "Es un número par."
                } else {
                    "Es un número impar."
                }
                // Muestra el resultado en un Toast
                Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingresa un número válido.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El campo no puede estar vacío.", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.mayorde3numeros


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numero1 = findViewById<EditText>(R.id.numero1)
        val numero2 = findViewById<EditText>(R.id.numero2)
        val numero3 = findViewById<EditText>(R.id.numero3)
        val botonCalcular = findViewById<Button>(R.id.botonCalcular)
        val resultado = findViewById<TextView>(R.id.resultado)

        botonCalcular.setOnClickListener {
            try {
                // Leer números ingresados
                val n1 = numero1.text.toString().toInt()
                val n2 = numero2.text.toString().toInt()
                val n3 = numero3.text.toString().toInt()

                // Determinar el mayor
                val mayor = when {
                    n1 >= n2 && n1 >= n3 -> n1
                    n2 >= n1 && n2 >= n3 -> n2
                    else -> n3
                }

                // Mostrar resultado
                resultado.text = "El número mayor es: $mayor"
            } catch (e: NumberFormatException) {
                // Manejo de errores si no se ingresaron números válidos
                resultado.text = "Por favor, ingrese números válidos."
            }
        }
    }
}

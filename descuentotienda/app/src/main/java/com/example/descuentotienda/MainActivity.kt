package com.example.descuentotienda

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la interfaz
        val etMonto: EditText = findViewById(R.id.etMonto)
        val btnCalcular: Button = findViewById(R.id.btnCalcular)
        val tvResultado: TextView = findViewById(R.id.tvResultado)

        // Configurar la acción del botón
        btnCalcular.setOnClickListener {
            val montoTexto = etMonto.text.toString()

            if (montoTexto.isNotEmpty()) {
                val monto = montoTexto.toDouble()
                val montoFinal = if (monto > 100) monto * 0.8 else monto

                // Mostrar el resultado
                tvResultado.text = String.format("Monto final: $%.2f", montoFinal)
            } else {
                tvResultado.text = "Por favor, ingrese un monto válido."
            }
        }
    }
}

package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

// Clase DepositDialog que extiende de Dialog para crear un cuadro de diálogo personalizado para depósitos
class DepositDialog(
    context: Context,  // Contexto de la actividad que crea este diálogo
    private val onDeposit: (Double) -> Unit  // Función que se ejecuta cuando se realiza un depósito con el monto especificado
) : Dialog(context) {

    // Método que se llama cuando el diálogo es creado
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout del diálogo con el diseño definido en dialog_deposit.xml
        setContentView(R.layout.dialog_deposit)

        // Vincula los botones del diálogo
        val btnDeposit10 = findViewById<Button>(R.id.btnDeposit10)
        val btnDeposit20 = findViewById<Button>(R.id.btnDeposit20)
        val btnDeposit50 = findViewById<Button>(R.id.btnDeposit50)
        val btnCancel = findViewById<Button>(R.id.btnCancelDeposit)

        // Configura el comportamiento del botón de depósito de 10 unidades
        btnDeposit10.setOnClickListener {
            onDeposit(10.0) // Llama a la función onDeposit con el valor de 10.0
            dismiss() // Cierra el diálogo después de realizar el depósito
        }

        // Configura el comportamiento del botón de depósito de 20 unidades
        btnDeposit20.setOnClickListener {
            onDeposit(20.0) // Llama a la función onDeposit con el valor de 20.0
            dismiss() // Cierra el diálogo después de realizar el depósito
        }

        // Configura el comportamiento del botón de depósito de 50 unidades
        btnDeposit50.setOnClickListener {
            onDeposit(50.0) // Llama a la función onDeposit con el valor de 50.0
            dismiss() // Cierra el diálogo después de realizar el depósito
        }

        // Configura el comportamiento del botón de cancelar
        btnCancel.setOnClickListener {
            dismiss() // Cierra el diálogo sin realizar ninguna acción
        }
    }
}

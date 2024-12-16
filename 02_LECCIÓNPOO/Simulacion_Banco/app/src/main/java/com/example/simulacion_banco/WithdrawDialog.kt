package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// Clase WithdrawDialog que extiende de Dialog para crear un cuadro de diálogo personalizado para retiros
class WithdrawDialog(
    context: Context,  // Contexto de la actividad que crea este diálogo
    private val onWithdraw: (Double) -> Unit  // Función que se ejecuta cuando se realiza un retiro con el monto especificado
) : Dialog(context) {

    // Método que se llama cuando el diálogo es creado
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout del diálogo con el diseño definido en dialog_withdraw.xml
        setContentView(R.layout.dialog_withdraw)

        // Vincula las vistas necesarias para el diálogo
        val editAmount = findViewById<EditText>(R.id.editAmount)  // Campo de texto para ingresar el monto a retirar
        val btnConfirm = findViewById<Button>(R.id.btnConfirmWithdraw)  // Botón para confirmar el retiro
        val btnCancel = findViewById<Button>(R.id.btnCancelWithdraw)  // Botón para cancelar el retiro

        // Configura el comportamiento del botón de confirmación de retiro
        btnConfirm.setOnClickListener {
            // Obtiene el monto ingresado como texto y lo convierte a un valor Double, o null si no es válido
            val amount = editAmount.text.toString().toDoubleOrNull()

            // Verifica si el monto es válido (mayor que 0)
            if (amount != null && amount > 0) {
                // Llama a la función onWithdraw con el monto válido
                onWithdraw(amount)
                dismiss()  // Cierra el diálogo después de realizar el retiro
            } else {
                // Si el monto no es válido, muestra un mensaje de error
                Toast.makeText(context, "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el comportamiento del botón de cancelación
        btnCancel.setOnClickListener {
            dismiss()  // Cierra el diálogo sin realizar ninguna acción
        }
    }
}

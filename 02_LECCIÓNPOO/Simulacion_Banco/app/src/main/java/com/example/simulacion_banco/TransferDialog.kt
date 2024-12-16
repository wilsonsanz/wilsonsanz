package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// Clase TransferDialog que extiende de Dialog para crear un cuadro de diálogo personalizado para realizar transferencias
class TransferDialog(
    context: Context,  // Contexto de la actividad que crea este diálogo
    private val onTransfer: (Double) -> Unit  // Función que se ejecuta cuando se realiza una transferencia con el monto especificado
) : Dialog(context) {

    // Método que se llama cuando el diálogo es creado
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout del diálogo con el diseño definido en dialog_transfer.xml
        setContentView(R.layout.dialog_transfer)

        // Vincula las vistas necesarias para el diálogo
        val editRecipient = findViewById<EditText>(R.id.editRecipient)  // Campo de texto para ingresar el destinatario de la transferencia
        val editAmount = findViewById<EditText>(R.id.editAmount)  // Campo de texto para ingresar el monto de la transferencia
        val btnConfirm = findViewById<Button>(R.id.btnConfirmTransfer)  // Botón para confirmar la transferencia
        val btnCancel = findViewById<Button>(R.id.btnCancelTransfer)  // Botón para cancelar la transferencia

        // Configura el comportamiento del botón de confirmación de transferencia
        btnConfirm.setOnClickListener {
            // Obtiene el destinatario y el monto ingresado como texto
            val recipient = editRecipient.text.toString()
            val amount = editAmount.text.toString().toDoubleOrNull()  // Convierte el monto a un número de tipo Double, o null si no es válido

            // Verifica si el destinatario no está vacío y el monto es válido (mayor que 0)
            if (recipient.isNotBlank() && amount != null && amount > 0) {
                // Llama a la función onTransfer con el monto válido
                onTransfer(amount)
                dismiss()  // Cierra el diálogo después de realizar la transferencia
            } else {
                // Si los datos no son válidos, muestra un mensaje de error
                Toast.makeText(context, "Por favor, ingrese datos válidos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el comportamiento del botón de cancelación
        btnCancel.setOnClickListener {
            dismiss()  // Cierra el diálogo sin realizar ninguna acción
        }
    }
}

package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class TransferDialog(
    context: Context,
    private val onTransfer: (Double) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_transfer)

        val editRecipient = findViewById<EditText>(R.id.editRecipient)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmTransfer)
        val btnCancel = findViewById<Button>(R.id.btnCancelTransfer)

        btnConfirm.setOnClickListener {
            val recipient = editRecipient.text.toString()
            val amount = editAmount.text.toString().toDoubleOrNull()

            if (recipient.isNotBlank() && amount != null && amount > 0) {
                onTransfer(amount)
                dismiss()
            } else {
                Toast.makeText(context, "Por favor, ingrese datos válidos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}

package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class WithdrawDialog(
    context: Context,
    private val onWithdraw: (Double) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_withdraw)

        val editAmount = findViewById<EditText>(R.id.editAmount)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmWithdraw)
        val btnCancel = findViewById<Button>(R.id.btnCancelWithdraw)

        btnConfirm.setOnClickListener {
            val amount = editAmount.text.toString().toDoubleOrNull()

            if (amount != null && amount > 0) {
                onWithdraw(amount)
                dismiss()
            } else {
                Toast.makeText(context, "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}

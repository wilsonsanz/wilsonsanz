package com.example.simulacion_banco

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast


class DepositDialog(
    context: Context,
    private val onDeposit: (Double) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_deposit)

        val btnDeposit10 = findViewById<Button>(R.id.btnDeposit10)
        val btnDeposit20 = findViewById<Button>(R.id.btnDeposit20)
        val btnDeposit50 = findViewById<Button>(R.id.btnDeposit50)
        val btnCancel = findViewById<Button>(R.id.btnCancelDeposit)

        btnDeposit10.setOnClickListener {
            onDeposit(10.0)
            dismiss()
        }

        btnDeposit20.setOnClickListener {
            onDeposit(20.0)
            dismiss()
        }

        btnDeposit50.setOnClickListener {
            onDeposit(50.0)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}

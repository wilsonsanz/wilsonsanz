package com.example.simulacion_banco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class DashboardActivity : AppCompatActivity() {

    private var saldo = 1200.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val saldoText = findViewById<TextView>(R.id.txtSaldo)
        val transferButton = findViewById<Button>(R.id.btnTransferir)
        val withdrawButton = findViewById<Button>(R.id.btnRetirar)
        val depositButton = findViewById<Button>(R.id.btnDepositar)
        val exitButton = findViewById<Button>(R.id.btnSalir)

        saldoText.text = "Saldo Actual: $${saldo}"

        transferButton.setOnClickListener {
            val dialog = TransferDialog(this) { monto ->
                if (monto <= saldo) {
                    saldo -= monto
                    saldoText.text = "Saldo Actual: $${saldo}"
                    Toast.makeText(this, "Transferencia exitosa", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        withdrawButton.setOnClickListener {
            val dialog = WithdrawDialog(this) { monto ->
                if (monto <= saldo) {
                    saldo -= monto
                    saldoText.text = "Saldo Actual: $${saldo}"
                    Toast.makeText(this, "Retiro exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        depositButton.setOnClickListener {
            val dialog = DepositDialog(this) { monto ->
                saldo += monto
                saldoText.text = "Saldo Actual: $${saldo}"
                Toast.makeText(this, "Depósito exitoso", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}

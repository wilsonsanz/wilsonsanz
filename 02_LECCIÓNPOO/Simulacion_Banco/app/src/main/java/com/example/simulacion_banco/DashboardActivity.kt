package com.example.simulacion_banco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class DashboardActivity : AppCompatActivity() {

    // Variable para almacenar el saldo inicial de la cuenta
    private var saldo = 1200.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout correspondiente a esta actividad
        setContentView(R.layout.activity_dashboard)

        // Vincula el TextView donde se muestra el saldo
        val saldoText = findViewById<TextView>(R.id.txtSaldo)
        // Vincula los botones para las diferentes acciones
        val transferButton = findViewById<Button>(R.id.btnTransferir)
        val withdrawButton = findViewById<Button>(R.id.btnRetirar)
        val depositButton = findViewById<Button>(R.id.btnDepositar)
        val exitButton = findViewById<Button>(R.id.btnSalir)

        // Muestra el saldo actual en el TextView
        saldoText.text = "Saldo Actual: $${saldo}"

        // Configura el botón de transferencia
        transferButton.setOnClickListener {
            // Muestra el diálogo para transferir dinero
            val dialog = TransferDialog(this) { monto ->
                // Verifica si el saldo es suficiente para la transferencia
                if (monto <= saldo) {
                    saldo -= monto // Resta el monto de la transferencia del saldo
                    saldoText.text = "Saldo Actual: $${saldo}" // Actualiza el TextView con el saldo nuevo
                    Toast.makeText(this, "Transferencia exitosa", Toast.LENGTH_SHORT).show() // Muestra un mensaje de éxito
                } else {
                    // Si el saldo es insuficiente, muestra un mensaje de error
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show() // Muestra el diálogo de transferencia
        }

        // Configura el botón de retiro
        withdrawButton.setOnClickListener {
            // Muestra el diálogo para retirar dinero
            val dialog = WithdrawDialog(this) { monto ->
                // Verifica si el saldo es suficiente para el retiro
                if (monto <= saldo) {
                    saldo -= monto // Resta el monto del retiro del saldo
                    saldoText.text = "Saldo Actual: $${saldo}" // Actualiza el TextView con el saldo nuevo
                    Toast.makeText(this, "Retiro exitoso", Toast.LENGTH_SHORT).show() // Muestra un mensaje de éxito
                } else {
                    // Si el saldo es insuficiente, muestra un mensaje de error
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show() // Muestra el diálogo de retiro
        }

        // Configura el botón de depósito
        depositButton.setOnClickListener {
            // Muestra el diálogo para depositar dinero
            val dialog = DepositDialog(this) { monto ->
                saldo += monto // Suma el monto del depósito al saldo
                saldoText.text = "Saldo Actual: $${saldo}" // Actualiza el TextView con el saldo nuevo
                Toast.makeText(this, "Depósito exitoso", Toast.LENGTH_SHORT).show() // Muestra un mensaje de éxito
            }
            dialog.show() // Muestra el diálogo de depósito
        }

        // Configura el botón de salida
        exitButton.setOnClickListener {
            finish() // Finaliza la actividad y regresa a la pantalla anterior
        }
    }
}

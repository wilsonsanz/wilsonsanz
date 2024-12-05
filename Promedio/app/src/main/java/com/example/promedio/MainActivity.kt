package com.example.promedio

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gradeInput: EditText = findViewById(R.id.gradeInput)
        val resultText: TextView = findViewById(R.id.resultText)

        // Botón para calcular el promedio
        val calculateAverageButton: Button = findViewById(R.id.calculateAverageButton)
        calculateAverageButton.setOnClickListener {
            val grades = mutableListOf<Int>()
            var total = 0
            var count = 0

            // Mostrar mensaje para indicar que el usuario puede ingresar varias calificaciones
            resultText.text = "Ingresa las calificaciones (ingresa -1 para terminar):"

            // Continuar solicitando calificaciones hasta que se ingrese -1
            while (true) {
                val gradeInputText = gradeInput.text.toString()
                val grade = gradeInputText.toIntOrNull()

                if (grade == null) {
                    // Si la entrada no es un número válido
                    resultText.text = "Por favor ingresa un número válido."
                    return@setOnClickListener
                }

                if (grade == -1) {
                    // Si el usuario ingresa -1, terminamos de pedir calificaciones
                    break
                }

                grades.add(grade)
                total += grade
                count++

                // Limpiar el campo de entrada para ingresar más calificaciones
                gradeInput.text.clear()
            }

            // Calcular el promedio
            if (count > 0) {
                val average = total.toDouble() / count
                resultText.text = "Promedio: %.2f".format(average)
            } else {
                resultText.text = "No se ingresaron calificaciones válidas."
            }
        }
    }
}

package com.example.sis_calificaciones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)  // Asegúrate de que este layout exista

        // Configuración de botones
        val btnEliminarAlumno: Button = findViewById(R.id.btn_eliminar_alumno)
        val btnConsultarNota: Button = findViewById(R.id.btn_consultar_nota)
        val btnModificarNota: Button = findViewById(R.id.btn_modificar_nota)
        val btnMostrarSuspensos: Button = findViewById(R.id.btn_mostrar_suspensos)
        val btnMostrarAprobados: Button = findViewById(R.id.btn_mostrar_aprobados)
        val btnCandidatosMH: Button = findViewById(R.id.btn_candidatos_mh)

        // Configurar las acciones de los botones
        btnEliminarAlumno.setOnClickListener {
            // Aquí puedes añadir la lógica para eliminar a un alumno
            Toast.makeText(this, "Eliminar alumno", Toast.LENGTH_SHORT).show()
        }

        btnConsultarNota.setOnClickListener {
            // Aquí puedes añadir la lógica para consultar la nota de un alumno
            Toast.makeText(this, "Consultar nota del alumno", Toast.LENGTH_SHORT).show()
        }

        btnModificarNota.setOnClickListener {
            // Aquí puedes añadir la lógica para modificar la nota de un alumno
            Toast.makeText(this, "Modificar nota del alumno", Toast.LENGTH_SHORT).show()
        }

        btnMostrarSuspensos.setOnClickListener {
            // Aquí puedes añadir la lógica para mostrar los suspensos
            Toast.makeText(this, "Mostrar suspensos", Toast.LENGTH_SHORT).show()
        }

        btnMostrarAprobados.setOnClickListener {
            // Aquí puedes añadir la lógica para mostrar los aprobados
            Toast.makeText(this, "Mostrar aprobados", Toast.LENGTH_SHORT).show()
        }

        btnCandidatosMH.setOnClickListener {
            // Aquí puedes añadir la lógica para mostrar candidatos a matrícula de honor
            Toast.makeText(this, "Mostrar candidatos a MH", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.sis_calificaciones

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val alumnosList = mutableListOf<Alumno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etDni = findViewById<EditText>(R.id.et_dni)
        val etApellidos = findViewById<EditText>(R.id.et_apellidos)
        val etNombre = findViewById<EditText>(R.id.et_nombre)
        val etNota = findViewById<EditText>(R.id.et_nota)
        val btnAddStudent = findViewById<Button>(R.id.btn_add_student)
        val btnOpenMenu = findViewById<Button>(R.id.btn_open_menu)
        val rvStudents = findViewById<RecyclerView>(R.id.rv_students)

        val adapter = AlumnoAdapter(alumnosList)
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnAddStudent.setOnClickListener {
            val dni = etDni.text.toString()
            val apellidos = etApellidos.text.toString()
            val nombre = etNombre.text.toString()
            val nota = etNota.text.toString().toFloatOrNull()

            if (dni.isEmpty() || apellidos.isEmpty() || nombre.isEmpty() || nota == null) {
                // Mostrar mensaje de error
                return@setOnClickListener
            }

            if (alumnosList.any { it.dni == dni }) {
                // Mostrar mensaje de error de DNI duplicado
                return@setOnClickListener
            }

            val alumno = Alumno(dni, apellidos, nombre, nota)
            alumnosList.add(alumno)
            adapter.notifyDataSetChanged()
        }

        btnOpenMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putParcelableArrayListExtra("alumnos", ArrayList(alumnosList))
            startActivity(intent)
        }
    }
}

private fun Intent.putParcelableArrayListExtra(s: String, arrayList: ArrayList<Alumno>) {

}

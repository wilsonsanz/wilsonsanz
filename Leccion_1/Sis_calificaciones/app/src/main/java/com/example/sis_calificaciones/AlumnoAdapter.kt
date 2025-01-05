package com.example.sis_calificaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Definir el adaptador para el RecyclerView
class AlumnoAdapter(private val alumnos: List<Alumno>) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    // Crear el ViewHolder que contiene las vistas para cada item
    inner class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDni: TextView = itemView.findViewById(R.id.tv_dni)
        val tvApellidos: TextView = itemView.findViewById(R.id.tv_apellidos)
        val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        val tvNota: TextView = itemView.findViewById(R.id.tv_nota)
        val tvCalificacion: TextView = itemView.findViewById(R.id.tv_calificacion)
    }

    // Crear un nuevo ViewHolder y asociarlo con el layout de cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    // Vincular los datos del alumno al ViewHolder
    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.tvDni.text = alumno.dni
        holder.tvApellidos.text = alumno.apellidos
        holder.tvNombre.text = alumno.nombre
        holder.tvNota.text = alumno.nota.toString()
        holder.tvCalificacion.text = alumno.calificacion
    }

    // Retornar el número de items en la lista de alumnos
    override fun getItemCount(): Int {
        return alumnos.size
    }
}

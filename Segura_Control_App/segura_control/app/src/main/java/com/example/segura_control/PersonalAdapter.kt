package com.example.segura_control

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.segura_control.sheets.Personal

class PersonalAdapter(
    private var listaPersonal: List<Personal> // CAMBIADO A 'var' y 'private'
) : RecyclerView.Adapter<PersonalAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        val tvUnidad: TextView = itemView.findViewById(R.id.tvUnidad)
        val tvEscuadron: TextView = itemView.findViewById(R.id.tvEscuadron)
        val tvDistrito: TextView = itemView.findViewById(R.id.tvDistrito)
        val tvSector: TextView = itemView.findViewById(R.id.tvSector)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_personal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personal = listaPersonal[position]
        holder.tvFecha.text = personal.FECHA
        holder.tvHora.text = personal.HORA
        holder.tvUnidad.text = personal.UNIDAD
        holder.tvEscuadron.text = personal.ESCUADRON
        holder.tvDistrito.text = personal.DISTRITO
        holder.tvSector.text = personal.SECTOR
        holder.tvDireccion.text = personal.DIRECCION



    }

    override fun getItemCount(): Int {
        return listaPersonal.size
    }


    fun actualizarLista(nuevaLista: List<Personal>) {
        listaPersonal = nuevaLista
        notifyDataSetChanged()
    }
}

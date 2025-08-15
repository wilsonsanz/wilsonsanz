package com.example.segura_control

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.segura_control.databinding.RutasTvBinding
import com.example.segura_control.sheets.Rutas

// 1. Definir la interfaz de callback
interface OnRutaDataChangedListener {
    // Cambiamos el nombre para reflejar que es una acción de "enviar" o "actualizar" final
    fun onRutaDataSent(position: Int, updatedRuta: Rutas)
}

class RutasAdapter(
    private var rutasList: List<Rutas>,
    private val listener: OnRutaDataChangedListener // 2. Añadir el listener al constructor
) : RecyclerView.Adapter<RutasAdapter.RutaViewHolder>() {

    inner class RutaViewHolder(private val binding: RutasTvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ruta: Rutas) {
            // Mapeo de los datos a los TextViews (no editables)
            // Asegúrate de manejar los nulos para estos TextViews también si vienen de Rutas?
            binding.tvDistri.text = " ${ruta.DISTRITO ?: "N/A"}"
            binding.tvTurno.text = " ${ruta.TURNO ?: "N/A"}"
            binding.tvGrupo.text = " ${ruta.GRUPO ?: "N/A"}"
            binding.tvMovil.text = " ${ruta.MOVIL}" // MOVIL no es anulable
            binding.tvNombre.text = "${ruta.NOMBRE_DEL_CONDUCTOR ?: "N/A"}"
            binding.tvRecorrido.text = "${ruta.RECORRIDO ?: "N/A"}"
            binding.tvConsignas.text = "${ruta.CONSIGNAS ?: "Ninguna"}"
            binding.tvCoordenadas.text = "${ruta.COORDENADAS ?: "N/A"}"

            // Establecer el texto inicial en los EditTexts
            // Usamos el operador Elvis para convertir String? a String si es null.
            binding.tvTelefono.setText(ruta.TELEFONO ?: "")
            binding.tvCopiloto.setText(ruta.COPILOTO ?: "")
            binding.tvCopiloto2.setText(ruta.COPILOTO_2 ?: "")

            // Manejar el clic del botón 'Enviar datos'
            binding.btnEnviarDatos.setOnClickListener {
                // Obtener los valores actuales de los EditTexts en el momento del clic
                val nuevoTelefono = binding.tvTelefono.text.toString().trim()
                val nuevoCopiloto1 = binding.tvCopiloto.text.toString().trim()
                val nuevoCopiloto2 = binding.tvCopiloto2.text.toString().trim()

                // Validaciones adicionales si es necesario antes de enviar
                if (nuevoTelefono.isEmpty() || nuevoCopiloto1.isEmpty()) {
                    Toast.makeText(itemView.context, "Por favor, complete al menos el Teléfono y Copiloto 1 antes de enviar.", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear una nueva instancia de Rutas con los datos actualizados
                    val updatedRuta = ruta.copy(
                        TELEFONO = nuevoTelefono,
                        COPILOTO = nuevoCopiloto1,
                        COPILOTO_2 = nuevoCopiloto2
                    )
                    // Notificar a la actividad (o al fragmento) que los datos han sido "enviados"
                    listener.onRutaDataSent(adapterPosition, updatedRuta)
                }
            }

            // --- Lógica para el clic en tvCoordenadas (integración de Google Maps) ---
            binding.tvCoordenadas.setOnClickListener {
                val inputData = ruta.COORDENADAS?.trim() // Obtiene la cadena de coordenadas o URL

                if (inputData.isNullOrEmpty()) {
                    Toast.makeText(it.context, "Información de ruta no disponible", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener // Salir si está vacío
                }

                val uri: Uri?
                val intent: Intent

                // Comprobar si es una URL (empieza con http/https)
                if (inputData.startsWith("http://") || inputData.startsWith("https://")) {
                    uri = Uri.parse(inputData) // Parsear la URL directamente
                    intent = Intent(Intent.ACTION_VIEW, uri)
                    // Para URLs (especialmente enlaces acortados de Maps),
                    // es mejor NO usar setPackage() para permitir que el sistema
                    // resuelva el deep link y abra la app de Maps si es la predeterminada.
                    // Si se usa setPackage(), podría interferir con la resolución del deep link.
                } else if (inputData.contains(",")) {
                    // Si contiene una coma, asumimos que son coordenadas (ej. "lat,lon")
                    uri = Uri.parse("geo:$inputData?q=$inputData")
                    intent = Intent(Intent.ACTION_VIEW, uri)
                    // Para coordenadas geo:, sí podemos forzar el paquete para asegurar Google Maps.
                    intent.setPackage("com.google.android.apps.maps")
                } else {
                    // Si no es URL ni coordenadas válidas, mostrar mensaje de error
                    Toast.makeText(it.context, "Formato de ruta no válido (espera URL o 'lat,lon')", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                try {
                    it.context.startActivity(intent)
                } catch (e: Exception) {
                    // Si Google Maps no está instalado o hay otro error,
                    // o si la URL no es manejada por ninguna app.
                    Toast.makeText(it.context, "No se pudo abrir la ruta en Google Maps. Asegúrate de tener la app instalada y que el enlace sea válido.", Toast.LENGTH_LONG).show()
                    e.printStackTrace() // Imprimir el stack trace para depuración
                }
            }
            // --- Fin de la lógica para el clic en tvCoordenadas ---
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaViewHolder {
        val binding = RutasTvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RutaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RutaViewHolder, position: Int) {
        val ruta = rutasList[position]
        holder.bind(ruta)
    }

    override fun getItemCount(): Int {
        return rutasList.size
    }

    // Método para actualizar la lista de rutas en el adaptador
    fun actualizarLista(nuevaLista: List<Rutas>) {
        this.rutasList = nuevaLista
        notifyDataSetChanged()
    }
}

package com.example.segura_control.script_get

import com.example.segura_control.sheets.Rutas
import com.google.gson.annotations.SerializedName

// Este es el modelo principal para la respuesta JSON completa
// Si tu script para "rutas" sigue devolviendo una clave "personal",
// entonces este modelo debe esperarla.
data class GetResponseRutas(
        @SerializedName("personal") // <--- ¡Importante! La clave en el JSON es "personal"
        val rutas: List<Rutas> // <--- Cambiamos el nombre de la lista para reflejar lo que contiene
)

// Este es el modelo para cada elemento individual dentro de la lista 'personal'
data class RutaItem(
        @SerializedName("DISTRITO")
        val distrito: String?, // Añadimos '?' porque los campos pueden venir vacíos
        @SerializedName("TURNO")
        val turno: String?,
        @SerializedName("GRUPO")
        val grupo: String?,
        @SerializedName("MOVIL")
        val movil: String?,
        @SerializedName("NOMBRE_DEL_CONDUCTOR")
        val nombreDelConductor: String?,
        @SerializedName("RECORRIDO")
        val recorrido: String?,
        @SerializedName("TELEFONO")
        val telefono: String?,
        @SerializedName("COPILOTO")
        val copiloto: String?,
        @SerializedName("COPILOTO_2")
        val copiloto2: String?,
        @SerializedName("COPILOTO_3")
        val copiloto3: String?, // Este campo también puede ser nulo o vacío
        @SerializedName("CONSIGNAS")
        val consignas: String?,
        @SerializedName("COORDENADAS")
        val coordenadas: String?
)

// Las clases Rutas y RutasData que tenías no son necesarias para parsear el JSON que me mostraste.
// Si las usas para otra funcionalidad (como enviar datos POST), deberías mantenerlas separadas.
// data class Rutas(...) // NO USAR para el GET actual
// data class RutasData(...) // NO USAR para el GET actual
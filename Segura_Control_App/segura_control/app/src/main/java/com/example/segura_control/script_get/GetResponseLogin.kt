package com.example.segura_control.script_get

import com.google.gson.annotations.SerializedName

// Este es el modelo principal para la respuesta JSON completa
// Si tu script para "rutas" sigue devolviendo una clave "personal",
// entonces este modelo debe esperarla.
data class GetResponseLogin(
    @SerializedName("personal") // <--- ¡Importante! La clave en el JSON es "personal"
    val Login: List<LoginItem> // <--- Cambiamos el nombre de la lista para reflejar lo que contiene
)

// Este es el modelo para cada elemento individual dentro de la lista 'personal'
data class LoginItem(
    @SerializedName("CONDUCTORES")
    val conductor: String?,
    @SerializedName("USUARIO")
    val usuario: String?,
    @SerializedName("CEDULA")
    val cedula: String?,
    @SerializedName("MOVIL")
    val movil: String?
)

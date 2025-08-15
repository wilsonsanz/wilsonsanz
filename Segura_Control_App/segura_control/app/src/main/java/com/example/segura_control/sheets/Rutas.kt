package com.example.segura_control.sheets


data class Rutas(
    val DISTRITO: String?, // Podría ser nulo si no siempre está presente
    val TURNO: String?,
    val GRUPO: String?,
    val MOVIL: String, // Este probablemente no debería ser nulo, ya que es tu clave
    val NOMBRE_DEL_CONDUCTOR: String?,
    val RECORRIDO: String?,
    val TELEFONO: String?, // <--- ¡CAMBIAR A String?!
    val COPILOTO: String?, // <--- ¡CAMBIAR A String?!
    val COPILOTO_2: String?, // <--- ¡CAMBIAR A String?! (Este es el que te da el error)
    val COPILOTO_3: String?, // <--- ¡CAMBIAR A String?!
    val CONSIGNAS: String?,
    val COORDENADAS: String?
)
data class RutasData(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)


package com.example.segura_control.sheets


data class datos(
    val MOVIL: String?,
    val TELEFONO: String?, // <--- ¡CAMBIAR A String?!
    val COPILOTO: String?, // <--- ¡CAMBIAR A String?!
    val COPILOTO_2: String?, // <--- ¡CAMBIAR A String?! (Este es el que te da el error)
    val COPILOTO_3: String?
)
data class datosData(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)

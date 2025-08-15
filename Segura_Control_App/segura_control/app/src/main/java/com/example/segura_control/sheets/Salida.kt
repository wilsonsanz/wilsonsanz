package com.example.segura_control.sheets

data class Salida(
    val MOVIL: String,
    val CONDUCTORES: String,
    val GRUPO: String,
    val HORA_SALIDA_DE_BASE: String,
    val KM_SALIDA_DE_BASE: String,
    val COMBUSTIBLE_17_GALONES: String,
    val COMENTARIOS: String,
    val FOTO: String
)

data class SalidaData(
    val action: String, // ¡Añadir esta línea de nuevo!
    val spreadsheet_id: String,
    val sheet: String, // Renómbrala a 'sheetName' para mayor claridad si quieres
    val rows: List<List<String>>
)
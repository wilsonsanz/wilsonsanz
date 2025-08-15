package com.example.segura_control.sheets

data class Wiki(
    val MOVIL: String,
    val WC: String,
    val WC2: String,
    val DESAYUNO: String,
    val ALMUERZO: String,
    val MERIENDA: String
)

data class WikiData(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
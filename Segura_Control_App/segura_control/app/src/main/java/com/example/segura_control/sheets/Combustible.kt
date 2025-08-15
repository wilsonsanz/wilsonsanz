package com.example.segura_control.sheets

data class Combustible(
    val MOVIL: String,
    val KILOMETRAJE_INICIAL: String,
    val TOTAL_GALONES: String
)

data class CombustibleData(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
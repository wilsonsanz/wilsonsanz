package com.example.segura_control.sheets

data class datos2(
    val CONDUCTORES: String?,
    val MOVIL: String?
)
data class datos2Data(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
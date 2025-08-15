package com.example.segura_control

data class Login_app(
    val CONDUCTORES: String,
    val USUARIO: String,
    val CEDULA: String,
    val MOVIL: String
)
data class Login_appData(
    val action: String,
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
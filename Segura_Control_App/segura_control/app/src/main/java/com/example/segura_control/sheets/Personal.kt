package com.example.segura_control.sheets

data class Personal(
    val FECHA: String,
    val HORA: String,
    val UNIDAD: String,
    val ESCUADRON: String,
    val DISTRITO: String,
    val SECTOR: String,
    val DIRECCION: String,
    val ACTIVIDAD: String,
    val INCIDENTE: String,
    val DETALLE: String,
    val FOTO: String
)

data class PersonalData(
    val spreadsheet_id: String,
    val sheet: String,
    val rows: List<List<String>>
)
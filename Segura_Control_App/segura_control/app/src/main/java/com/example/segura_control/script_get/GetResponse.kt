package com.example.segura_control.script_get

import com.example.segura_control.sheets.Combustible
import com.example.segura_control.sheets.Ingreso
import com.example.segura_control.sheets.Personal
import com.example.segura_control.sheets.Rutas
import com.example.segura_control.sheets.Salida

data class GetResponse(
    val personal: List<Personal>,
    val ingreso: List<Ingreso>,
    val salida: List<Salida>,
    val combustible: List<Combustible>,
    val rutas: List<Rutas>

)

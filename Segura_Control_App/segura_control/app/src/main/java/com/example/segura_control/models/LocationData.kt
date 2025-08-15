package com.example.segura_control.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LocationData(
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val conductor: String? = null,
    val movil: String? = null,
    val precision: Float = 0.0f, // Añadimos precisión
    @ServerTimestamp // Esto le dice a Firestore que añada la marca de tiempo del servidor
    val timestamp: Date? = null
)
package com.example.sis_calificaciones

data class Alumno(
    val dni: String,
    val apellidos: String,
    val nombre: String,
    var nota: Float
) {
    var calificacion: String = calcularCalificacion(nota)

    private fun calcularCalificacion(nota: Float): String {
        return when {
            nota < 5 -> "SS"
            nota < 7 -> "AP"
            nota < 9 -> "NT"
            else -> "SB"
        }
    }

    fun actualizarNota(nuevaNota: Float) {
        nota = nuevaNota
        calificacion = calcularCalificacion(nota)
    }
}

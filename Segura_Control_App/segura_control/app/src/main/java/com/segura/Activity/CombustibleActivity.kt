package com.segura.Activity

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.BaseUrl
import com.example.segura_control.sheets.Combustible
import com.example.segura_control.sheets.CombustibleData
import com.example.segura_control.Constantes
import com.example.segura_control.R
import com.example.segura_control.RetrofitClient
import com.example.segura_control.databinding.CombustibleActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CombustibleActivity : AppCompatActivity() {

    private lateinit var binding: CombustibleActivityBinding

    private var listaCombustible: List<Combustible> = emptyList()

    private var numeroMovilRecibido: String? = null // Se declara aquí
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CombustibleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // **PASO 1: RECUPERAR EL NÚMERO DEL INTENT**
        numeroMovilRecibido = intent.getStringExtra("numeroRegistro")


        // **PASO 2: ASIGNAR EL NÚMERO AL EditText etMovil**
        if (!numeroMovilRecibido.isNullOrEmpty()) {
            binding.btnMovi.setText(numeroMovilRecibido)
            // Opcional: Si quieres que el campo no sea editable después de establecer el número
            binding.btnMovi.isEnabled = false
        } else {
            // Manejar el caso donde no se recibe el número (ej. si se abre esta Activity directamente)
            Toast.makeText(this, "Número de móvil no recibido.", Toast.LENGTH_SHORT).show()
        }

        binding.btnAgregar.setOnClickListener {
            agregarDataCombustible()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }



    private fun agregarDataCombustible() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val combustible = CombustibleData(
                    action = "agregarCombustible",
                    spreadsheet_id = Constantes.google_sheet_id,
                    sheet = Constantes.sheet4,
                    rows = listOf(
                        listOf(
                            binding.btnMovi.text.toString().trim(),
                            binding.btnKILOMETRAJE.text.toString().trim(),
                            binding.btnTotal.text.toString().trim()
                        )
                    )
                )
                val response = RetrofitClient.webService(BaseUrl.base_url_post).agregarCombustible(combustible)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CombustibleActivity, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

                        // codigo para vibrar
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(200)
                        }

                        // Sonido
                        // Si ya hay un MediaPlayer reproduciéndose o preparado, lo libera primero.
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(this@CombustibleActivity,
                            R.raw.messenger_tone
                        ) // Asegúrate que 'messenger_tone' coincide con el nombre de tu archivo sin extensión
                        mediaPlayer?.setOnCompletionListener { mp ->
                            // Opcional: liberar el reproductor una vez que el sonido haya terminado de reproducirse
                            mp.release()
                            mediaPlayer = null
                        }
                        mediaPlayer?.start() // Inicia la reproducción
                        // --- FIN: Código para hacer vibrar y sonar el dispositivo ---
                        binding.btnMovi.text?.clear()
                        binding.btnKILOMETRAJE.text?.clear()
                        binding.btnTotal.text?.clear()

                        obtenerData()
                    } else {
                        Toast.makeText(this@CombustibleActivity, "Error al enviar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CombustibleActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()

                }

            }
        }

    }

    private fun obtenerData() {
        // Aquí tu lógica para actualizar datos si lo necesitas
    }

}
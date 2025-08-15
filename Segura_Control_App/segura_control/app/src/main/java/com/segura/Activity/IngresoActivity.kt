package com.segura.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.BaseUrl
import com.example.segura_control.Constantes
import com.example.segura_control.sheets.IngresoData
import com.example.segura_control.R
import com.example.segura_control.RetrofitClient
import com.example.segura_control.databinding.IngresoActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IngresoActivity : AppCompatActivity() {

    private lateinit var binding: IngresoActivityBinding

    private var numeroMovilRecibido: String? = null
    private var nombreConductorRecibido: String? = null // Nueva variable para el nombre del conductor
    private var photoUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null


    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedPhotoUrl = result.data?.getStringExtra("photo_url")
            receivedPhotoUrl?.let {
                photoUrl = it // Almacenar la URL
                binding.etPhotoUrl.setText(it) // Mostrar la URL en el EditText (asumiendo que existe)
                Toast.makeText(this, "Foto URL recibida: $it", Toast.LENGTH_LONG).show()
            } ?: run {
                Toast.makeText(this, "No se recibió ninguna URL de foto.", Toast.LENGTH_SHORT).show()
                photoUrl = null // Asegurarse de que la URL se limpia si no se recibe
                binding.etPhotoUrl.text.clear()
            }
        } else {
            Toast.makeText(this, "Toma de foto cancelada o fallida.", Toast.LENGTH_SHORT).show()
            photoUrl = null // Asegurarse de que la URL se limpia
            binding.etPhotoUrl.text.clear()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IngresoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar el número de registro del Intent
        numeroMovilRecibido = intent.getStringExtra("numeroRegistro")
        // **RECUPERAR EL NOMBRE DEL CONDUCTOR DEL INTENT**
        nombreConductorRecibido = intent.getStringExtra("nombreConductor")

        // Asignar el número recuperado al EditText btnMovil
        if (!numeroMovilRecibido.isNullOrEmpty()) {
            binding.btnMovil.setText(numeroMovilRecibido)
            binding.btnMovil.isEnabled = false // Opcional: deshabilitar el campo
        } else {
            Toast.makeText(this, "Número de móvil no recibido.", Toast.LENGTH_SHORT).show()
        }

        // **ASIGNAR EL NOMBRE DEL CONDUCTOR AL EditText btnCONDUCTOR**
        if (!nombreConductorRecibido.isNullOrEmpty()) {
            binding.btnCONDUCTOR.setText(nombreConductorRecibido)
            binding.btnCONDUCTOR.isEnabled = false // Opcional: deshabilitar el campo
        } else {
            Toast.makeText(this, "Nombre de conductor no recibido.", Toast.LENGTH_SHORT).show()
        }


        val spinnerGrupo = listOf("A", "B", "C")
        val adapterGrupo = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerGrupo)
        adapterGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGrupo.adapter = adapterGrupo

        // Establecer la hora actual al btnHORADESALIDA al iniciar la actividad
        setCurrentTimeForDeparture()

        binding.btnAgregarfoto.setOnClickListener {
            val intent = Intent(this, FotoActivity::class.java)
            // Usar el launcher para iniciar la actividad y esperar un resultado
            takePhotoLauncher.launch(intent)
        }

        binding.btnAgregar.setOnClickListener {
            agregarDataIngreso()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setCurrentTimeForDeparture() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(calendar.time)
        binding.btnHORADESALIDA.setText(currentTime)
    }

    private fun agregarDataIngreso() {
        binding.btnAgregar.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movil = binding.btnMovil.text.toString().trim()
                val conductor = binding.btnCONDUCTOR.text.toString().trim()
                val kilometraje = binding.btnKILOMETRAJE.text.toString().trim()

                if (movil.isEmpty() || conductor.isEmpty() || kilometraje.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@IngresoActivity, "Por favor, completa los campos obligatorios (Móvil, Conductor, Kilometraje).", Toast.LENGTH_LONG).show()
                        binding.btnAgregar.isEnabled = true
                    }
                    return@launch
                }

                val ingreso = IngresoData(
                    action = "agregarIngreso",
                    spreadsheet_id = Constantes.google_sheet_id,
                    sheet = Constantes.sheet2,
                    rows = listOf(
                        listOf(
                            movil,
                            conductor, // Este ahora contendrá el valor pasado del conductor
                            binding.spinnerGrupo.selectedItem.toString(),
                            binding.btnHORADESALIDA.text.toString().trim(),
                            kilometraje,
                            binding.btnCOMBUSTIBLE.text.toString().trim(),
                            binding.etComentarios.text.toString().trim(),
                            photoUrl ?: ""
                        )
                    )
                )

                val response = RetrofitClient.webService(BaseUrl.base_url_post).agregarIngreso(ingreso)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@IngresoActivity, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

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
                        mediaPlayer = MediaPlayer.create(this@IngresoActivity, R.raw.messenger_tone) // Asegúrate que 'messenger_tone' coincide con el nombre de tu archivo sin extensión
                        mediaPlayer?.setOnCompletionListener { mp ->
                            // Opcional: liberar el reproductor una vez que el sonido haya terminado de reproducirse
                            mp.release()
                            mediaPlayer = null
                        }
                        mediaPlayer?.start() // Inicia la reproducción
                        // --- FIN: Código para hacer vibrar y sonar el dispositivo ---


                        // Limpieza de campos después de un envío exitoso
                        // binding.btnMovil.text.clear() // Comentado para mantener el móvil fijo
                        binding.btnCONDUCTOR.text.clear() // Si el conductor es fijo, comenta esta línea
                        binding.spinnerGrupo.setSelection(0)
                        setCurrentTimeForDeparture()
                        binding.btnKILOMETRAJE.text.clear()
                        binding.btnCOMBUSTIBLE.text.clear()
                        binding.etComentarios.text.clear()
                        binding.etPhotoUrl.text.clear()

                        obtenerData()
                    } else {
                        Toast.makeText(this@IngresoActivity, "Error al enviar: ${response.code()}. Revisa tu conexión o el script.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@IngresoActivity, "Excepción al enviar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnAgregar.isEnabled = true
                }
            }
        }
    }

    private fun obtenerData() {
        // Lógica para actualizar datos, si es necesario.
    }
}
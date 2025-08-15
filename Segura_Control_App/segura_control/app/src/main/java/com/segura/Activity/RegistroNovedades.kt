package com.segura.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils // Importar TextUtils para ellipsize
import android.widget.ArrayAdapter
import android.widget.EditText // Importar EditText
import android.widget.LinearLayout // Importar LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.R
import com.example.segura_control.databinding.RegistroNovedadesBinding
import com.example.segura_control.BaseUrl
import com.example.segura_control.Constantes
import com.example.segura_control.PersonalAdapter
import com.example.segura_control.RetrofitClient
import com.example.segura_control.sheets.Personal
import com.example.segura_control.sheets.PersonalData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroNovedades : AppCompatActivity() {

    private lateinit var binding: RegistroNovedadesBinding
    private lateinit var adapter: PersonalAdapter
    private lateinit var especialidad: String
    // No necesitamos una sola photoUrl, ahora tendremos una lista de URLs para múltiples campos.
    // private var photoUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null

    private var listaPersonal: List<Personal> = emptyList()

    // Lista para almacenar las referencias a todos los EditText de fotos
    private val photoUrlEditTexts = mutableListOf<EditText>()


    // Modificación del launcher para manejar la adición de una nueva URL a la lista
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedPhotoUrl = result.data?.getStringExtra("photo_url")
            receivedPhotoUrl?.let { url ->
                // Encuentra el último EditText de foto y establece su texto
                // O crea uno nuevo si es el primero o si el último ya tiene texto
                val currentPhotoEditText = photoUrlEditTexts.lastOrNull()
                if (currentPhotoEditText == null || currentPhotoEditText.text.toString().trim().isNotEmpty()) {
                    // Si no hay campos o el último ya está lleno, agrega un nuevo campo
                    addPhotoUrlField(url) // Agrega un nuevo campo con la URL recibida
                } else {
                    // Si el último campo está vacío, úsalo
                    currentPhotoEditText.setText(url)
                }
                Toast.makeText(this, "Foto URL recibida: $url", Toast.LENGTH_LONG).show()
            } ?: run {
                Toast.makeText(this, "No se recibió ninguna URL de foto.", Toast.LENGTH_SHORT).show()
                // Si el último campo está vacío, límpialo al no recibir URL
                photoUrlEditTexts.lastOrNull()?.text?.clear()
            }
        } else {
            Toast.makeText(this, "Toma de foto cancelada o fallida.", Toast.LENGTH_SHORT).show()
            // Si el último campo está vacío, límpialo al cancelar
            photoUrlEditTexts.lastOrNull()?.text?.clear()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistroNovedadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el primer EditText de URL de foto y añadirlo a la lista
        photoUrlEditTexts.add(binding.etPhotoUrl)

        // **CAMBIO IMPORTANTE AQUÍ:**
        // 1. Recuperar el número de registro del Intent
        val numeroDeUnidad = intent.getStringExtra("numeroRegistro")

        // 2. Asignar el número recuperado a etUnidad
        if (!numeroDeUnidad.isNullOrEmpty()) {
            binding.etUnidad.setText(numeroDeUnidad)
            // Opcional: si quieres que el campo sea solo de lectura después de establecerlo
            binding.etUnidad.isEnabled = false
        } else {
            // Manejar el caso donde no se recibe el número (ej. por si se abre esta Activity directamente)
            Toast.makeText(this, "Número de unidad no recibido.", Toast.LENGTH_SHORT).show()
        }

        // Configurar Spinners... (tu código existente para Spinners)
        val escuadrones = listOf("CORREDOR SEGURO", "DELTA", "EAS")
        val distritos = listOf("DISTRITOS", "9 DE OCTUBRE", "MODELO", "CEIBOS", "FLORIDA", "ESTEROS", "PASCUALES", "PORTETE", "NUEVA PROSPERINA", "SUR")
        val sectores = listOf("SECTORES", "SAMANES", "KENNEDY", "CEIBOS", "SAUCES", "ALBORADA", "GUAYACANES", "MARTHA DE ROLDOS", "SIMON BOLIVAR", "URDESA", "OLMEDO", "VERGELES", "CERRO DEL CARMEN", "SAN FELIPE", "METROPOLIS", "PUERTO SANTA ANA", "ROCAFUERTE")
        val actividades = listOf("PUNTO MARTILLO", "NOVEDAD EN CIRCULACION", "LOCAL SEGURO", "COLABORACION", "OPERATIVO VIA PUBLICA", "REVISION A PERSONA")
        val novedades = listOf("PRESENCIA DE AGENTE MUNICIPAL", "RETIRO DE INFORMALES", "SECUESTRO", "VEHICULO ABANDONADO", "ACCIDENTE DE TRANSITO LEVE", "ACCIDENTE DE TRANSITO GRAVE", "AGRESION A PERSONA", "VIOLENCIA FAMILIAR", "AMENAZA DE BOMBA", "CAPTURADO POR CIVILES", "DAÑO AL BIEN PUBLICO", "DENUNCIA DE ROBO", "DESALOJO", "EMERGENCIA MEDICA", "ESCANDALO", "LIBADORES", "CONSUMIDORES", "RETIRO DE INDIGENTES", "FRAUDE A PERSONA", "SOSPECHOSOS")

        binding.spinnerSector.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sectores).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerDistrito.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, distritos).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerEscuadron.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, escuadrones).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerNovedad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, novedades).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerActividad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        especialidad = intent.getStringExtra("especialidad") ?: ""
        val indexActividad = actividades.indexOfFirst { it.equals(especialidad, ignoreCase = true) }
        if (indexActividad != -1) {
            binding.spinnerActividad.setSelection(indexActividad)
        }

        adapter = PersonalAdapter(listaPersonal)
        binding.rvPersonal.adapter = adapter

        obtenerData()

        // Listener para el botón "Agregar Foto" (el original que abre la actividad de cámara)
        binding.btnAgregarfoto.setOnClickListener {
            val intent = Intent(this, FotoActivity::class.java)
            takePhotoLauncher.launch(intent)
        }

        // Listener para el nuevo botón de "más (+)"
        binding.btnAddPhotoField.setOnClickListener {
            addPhotoUrlField() // Llama a la función para añadir un nuevo campo
        }

        binding.btnAgregar.setOnClickListener {
            val unidad = binding.etUnidad.text.toString().trim()
            val escuadron = binding.spinnerEscuadron.selectedItem.toString()
            val distrito = binding.spinnerDistrito.selectedItem.toString()
            val sector = binding.spinnerSector.selectedItem.toString()
            val direccion = binding.etDireccion.text.toString().trim()
            val actividad = binding.spinnerActividad.selectedItem.toString()
            val novedad = binding.spinnerNovedad.selectedItem.toString()

            val defaultDistrito = "DISTRITOS"
            val defaultSector = "SECTORES"

            if (unidad.isEmpty() || distrito == defaultDistrito || sector == defaultSector ||
                direccion.isEmpty() || actividad.isEmpty() || novedad.isEmpty()
            ) {
                Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                binding.etFecha.setText(fechaActual)
                binding.etHora.setText(horaActual)

                agregarData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setupRecyclerView() {
        adapter = PersonalAdapter(listaPersonal)
        binding.rvPersonal.adapter = adapter
    }

    fun obtenerData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService(BaseUrl.base_url_get).obtenerTodoPersonal()

            if (response.isSuccessful) {
                listaPersonal = response.body()?.personal ?: emptyList()

                withContext(Dispatchers.Main) {
                    adapter.actualizarLista(listaPersonal)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroNovedades, "Error al obtener datos: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Función para añadir dinámicamente un nuevo campo de URL de foto
    private fun addPhotoUrlField(initialUrl: String = "") {
        val newEtPhotoUrl = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Margen inferior para separar los EditText
                setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.margin_small)) // Asegúrate de tener una dimensión llamada margin_small
            }
            hint = "URL de la Foto Adicional"
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
            setPadding(8, 8, 8, 8)
            setBackgroundResource(android.R.drawable.editbox_background)
            setTextColor(resources.getColor(android.R.color.black, theme))
            setHintTextColor(resources.getColor(android.R.color.darker_gray, theme))
            setText(initialUrl) // Establece la URL inicial si se proporciona
        }

        // Añade el nuevo EditText al layout_fotos antes del botón de añadir
        // El childCount - 1 asegura que se añada antes del btnAddPhotoField
        binding.layoutFotos.addView(newEtPhotoUrl, binding.layoutFotos.childCount - 1)
        photoUrlEditTexts.add(newEtPhotoUrl) // Añade a la lista de seguimiento
    }

    fun agregarData() {
        binding.btnAgregar.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Recopilar todas las URLs de los campos de foto
                val allPhotoUrls = photoUrlEditTexts.map { it.text.toString().trim() }.filter { it.isNotEmpty() }
                // Une las URLs con un separador (ej. una coma o espacio) si vas a guardarlas en un solo campo
                // O si tu API soporta múltiples URLs, puedes enviarlas como una lista
                val combinedPhotoUrls = allPhotoUrls.joinToString(separator = " - ")

                val personalData = PersonalData(
                    spreadsheet_id = Constantes.google_sheet_id,
                    sheet = Constantes.sheet,
                    rows = listOf(
                        listOf(
                            binding.etFecha.text.toString(),
                            binding.etHora.text.toString(),
                            binding.etUnidad.text.toString(),
                            binding.spinnerEscuadron.selectedItem.toString(),
                            binding.spinnerDistrito.selectedItem.toString(),
                            binding.spinnerSector.selectedItem.toString(),
                            binding.etDireccion.text.toString(),
                            binding.spinnerActividad.selectedItem.toString(),
                            binding.spinnerNovedad.selectedItem.toString(),
                            binding.etComentarios.text.toString(),
                            combinedPhotoUrls // Envía todas las URLs combinadas
                        )
                    )
                )

                val response = RetrofitClient.webService(BaseUrl.base_url_post).agregarPersonal(personalData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegistroNovedades, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(200)
                        }

                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(this@RegistroNovedades, R.raw.tone)
                        mediaPlayer?.setOnCompletionListener { mp ->
                            mp.release()
                            mediaPlayer = null
                        }
                        mediaPlayer?.start()

                        // Limpiar todos los campos de entrada, incluyendo los EditText de fotos dinámicos
                        clearInputFields()
                        obtenerData()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (errorBody.isNullOrBlank()) {
                            "Error al enviar: ${response.code()}. Revisa tu conexión o el script."
                        } else {
                            "Error al enviar (${response.code()}): $errorBody"
                        }
                        Toast.makeText(this@RegistroNovedades, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroNovedades, "Excepción de red/envío: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnAgregar.isEnabled = true
                }
            }
        }
    }

    // Función para limpiar todos los campos, incluyendo los EditText de fotos dinámicos
    private fun clearInputFields() {
        binding.etFecha.text.clear()
        binding.etHora.text.clear()
        binding.spinnerEscuadron.setSelection(0)
        binding.spinnerDistrito.setSelection(0)
        binding.spinnerSector.setSelection(0)
        binding.etDireccion.text.clear()
        binding.spinnerActividad.setSelection(0)
        binding.etComentarios.text.clear()
        binding.spinnerNovedad.setSelection(0)

        // Eliminar todos los EditText de fotos dinámicos y limpiar la lista
        for (i in photoUrlEditTexts.size - 1 downTo 1) { // Iterar de atrás hacia adelante para no romper el índice
            binding.layoutFotos.removeView(photoUrlEditTexts[i])
            photoUrlEditTexts.removeAt(i)
        }
        // Limpiar el EditText original
        binding.etPhotoUrl.text.clear()
    }
}
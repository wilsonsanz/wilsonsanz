package com.segura.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.segura_control.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.google.gson.annotations.SerializedName // Para Gson

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit // Para OkHttpClient timeouts

// --- Interfaz de Imgur API ---
interface ImgurApiService {
    @Headers("Authorization: Client-ID $IMGUR_CLIENT_ID") // Reemplaza con tu Client-ID
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): ImgurUploadResponse
}

// --- Modelos de Datos para la Respuesta de Imgur ---
data class ImgurUploadResponse(
    @SerializedName("data") val data: ImgurData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImgurData(
    @SerializedName("id") val id: String,
    @SerializedName("link") val link: String, // Esta es la URL pública que necesitamos
    @SerializedName("deletehash") val deletehash: String? // Hash para eliminar la imagen
)

// --- CONSTANTE DE IM GUR
const val IMGUR_CLIENT_ID = "cc3d1d0debf9ac0" 

class FotoActivity : AppCompatActivity() {

    private lateinit var imageViewCaptured: ImageView
    private lateinit var textViewPhotoPath: TextView
    private lateinit var btnDone: Button

    private var currentPhotoPath: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imgurApiService: ImgurApiService // Cliente para la API de Imgur

    // Constantes para solicitudes de permisos
    private val REQUEST_CODE_PERMISSIONS = 101

    // Para manejar el resultado de tomar la foto (API 30+)
    private val takePictureResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            currentPhotoPath?.let {
                processAndDisplayPhoto(it)
            }
        } else {
            Toast.makeText(this, "La toma de foto fue cancelada o falló.", Toast.LENGTH_SHORT).show()
            currentPhotoPath = null
            finish() // Cierra la actividad si la foto no se toma
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)

        // Configuración de Retrofit para Imgur API
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Para ver logs detallados de la petición
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la conexión
            .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de espera para la lectura de la respuesta
            .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de espera para la escritura de la petición
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgur.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        imgurApiService = retrofit.create(ImgurApiService::class.java)

        imageViewCaptured = findViewById(R.id.imageViewCaptured)
        textViewPhotoPath = findViewById(R.id.textViewPhotoPath)
        btnDone = findViewById(R.id.btnDone)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // El botón "Done" se deshabilitará al inicio y durante la subida.
        btnDone.isEnabled = false
        btnDone.setOnClickListener {
            // Este listener podría mostrar un mensaje si la subida aún no ha terminado.
            Toast.makeText(this, "Por favor, espera a que la foto termine de subirse...", Toast.LENGTH_SHORT).show()
        }

        checkPermissionsAndTakePicture()
    }

    // --- Permisos ---
    private fun checkPermissionsAndTakePicture() {
        val permissionsToRequest = mutableListOf<String>()

        // Permisos de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // Permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Permisos de almacenamiento (manejo según la versión de Android)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // Android 9 y anteriores
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            // Para Android 13+, READ_MEDIA_IMAGES es para acceder a la galería,
            // pero para guardar una foto tomada con la cámara, no siempre es necesario si usas getExternalFilesDir.
            // Sin embargo, si la app necesita leer las imágenes de la galería global, es útil.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            if (isLocationEnabled()) {
                dispatchTakePictureIntent()
            } else {
                showLocationAlertDialog()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                if (isLocationEnabled()) {
                    dispatchTakePictureIntent()
                } else {
                    showLocationAlertDialog()
                }
            } else {
                Toast.makeText(this, "Permisos denegados. No se puede tomar la foto con todas las funcionalidades.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    // --- Toma de Foto ---
    private fun dispatchTakePictureIntent() {
        currentPhotoPath = null // Limpiar la ruta anterior

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error al crear el archivo de imagen: ${ex.message}", Toast.LENGTH_LONG).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureResult.launch(photoURI)
                }
            } ?: run {
                Toast.makeText(this, "No se encontró una aplicación de cámara.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir == null || !storageDir.exists()) {
            storageDir?.mkdirs()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // --- Procesamiento de la Foto (Marca de Agua, Fecha, Hora, Ubicación) ---
    private fun processAndDisplayPhoto(photoPath: String) {
        val originalBitmap = BitmapFactory.decodeFile(photoPath)

        if (originalBitmap == null) {
            Toast.makeText(this, "Error: No se pudo cargar la imagen original.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val rotatedBitmap = rotateBitmapIfNecessary(originalBitmap, photoPath)

        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDateTime = dateTimeFormat.format(Date())

        getLocation { latitude, longitude ->
            val locationString = if (latitude != null && longitude != null) {
                "Lat: ${String.format("%.6f", latitude)}, Lon: ${String.format("%.6f", longitude)}"
            } else {
                "Ubicación no disponible"
            }

            val processedBitmap = addTextAndWatermarkToBitmap(rotatedBitmap, currentDateTime, locationString, "SEGURAEP")
            saveProcessedPhoto(processedBitmap, photoPath) // Guarda la foto procesada localmente

            imageViewCaptured.setImageBitmap(processedBitmap)
            textViewPhotoPath.text = "Ruta local: $photoPath"
            galleryAddPic(photoPath)

            // --- INICIA SUBIDA A IMGUR ---
            btnDone.isEnabled = false // Deshabilitar el botón mientras se sube
            uploadPhotoToImgur(photoPath)
        }
    }

    private fun rotateBitmapIfNecessary(bitmap: Bitmap, photoPath: String): Bitmap {
        val ei = androidx.exifinterface.media.ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun addTextAndWatermarkToBitmap(originalBitmap: Bitmap, dateTime: String, location: String, watermarkText: String): Bitmap {
        val bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        paint.color = Color.WHITE
        paint.textSize = (bitmap.height / 30).toFloat()
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.setShadowLayer(5f, 0f, 0f, Color.BLACK)

        val margin = (bitmap.height * 0.03).toFloat()

        canvas.drawText(dateTime, margin, bitmap.height - margin * 3, paint)
        canvas.drawText(location, margin, bitmap.height - margin, paint)

        val watermarkPaint = Paint(paint)
        watermarkPaint.color = Color.parseColor("#80FFFFFF")
        watermarkPaint.textSize = (bitmap.height / 15).toFloat()
        watermarkPaint.textAlign = Paint.Align.RIGHT
        watermarkPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        watermarkPaint.setShadowLayer(5f, 0f, 0f, Color.BLACK)

        canvas.drawText(watermarkText, bitmap.width - margin, margin + watermarkPaint.textSize, watermarkPaint)

        return bitmap
    }

    private fun saveProcessedPhoto(bitmap: Bitmap, originalPath: String) {
        val file = File(originalPath)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            Toast.makeText(this, "Foto guardada localmente y procesada.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la foto procesada: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // --- Subida a Imgur ---
    private fun uploadPhotoToImgur(photoPath: String) {
        val photoFile = File(photoPath)
        if (!photoFile.exists()) {
            Toast.makeText(this, "Error: El archivo de foto no existe para subir a Imgur.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Convertir la imagen a un RequestBody
        val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", photoFile.name, requestFile)

        // Usar CoroutineScope para la llamada a la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FotoActivity, "Subiendo foto a Imgur...", Toast.LENGTH_SHORT).show()
                }

                val response = imgurApiService.uploadImage(body)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        val downloadUrl = response.data.link
                        Toast.makeText(this@FotoActivity, "Foto subida a Imgur exitosamente.", Toast.LENGTH_LONG).show()
                        textViewPhotoPath.text = "URL de la foto: $downloadUrl" // Actualiza el TextView con la URL

                        // Devolver la URL a la actividad que llamó
                        val resultIntent = Intent()
                        resultIntent.putExtra("photo_url", downloadUrl)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish() // Cierra esta actividad
                    } else {
                        Toast.makeText(this@FotoActivity, "Error al subir foto a Imgur: ${response.status}", Toast.LENGTH_LONG).show()
                        btnDone.isEnabled = true
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FotoActivity, "Excepción al subir foto a Imgur: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    btnDone.isEnabled = true
                    finish()
                }
            }
        }
    }

    // --- Ubicación ---
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Activar Ubicación")
            .setMessage("Tu ubicación (GPS) no está activada. Por favor, actívala para poder registrar la ubicación en la foto.")
            .setPositiveButton("Configuración") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Ubicación requerida para esta función. Saliendo.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun getLocation(callback: (Double?, Double?) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        callback(location.latitude, location.longitude)
                    } else {
                        callback(null, null)
                        Toast.makeText(this, "Ubicación no disponible. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    callback(null, null)
                    Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            callback(null, null)
            Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Notificar a la Galería ---
    private fun galleryAddPic(photoPath: String) {
        val file = File(photoPath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri: Uri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
        } else {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(photoPath)
                mediaScanIntent.data = Uri.fromFile(f)
                sendBroadcast(mediaScanIntent)
            }
        }
    }

    override fun onBackPressed() {
        if (currentPhotoPath == null || !btnDone.isEnabled) {
            Toast.makeText(this, "Por favor, espera a que la foto se suba o toma una foto antes de salir.", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }
}

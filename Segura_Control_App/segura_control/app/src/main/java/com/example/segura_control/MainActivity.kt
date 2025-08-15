package com.example.segura_control

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch // Importa la clase Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import android.Manifest

// Importa tu nuevo servicio
import com.example.segura_control.services.LocationTrackingService
import com.example.segura_control.script_get.GetResponseLogin
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.segura.Activity.MenuActivity

interface ApiServiceLogin {
    @GET("exec?spreadsheetId=13UyqITFu8DGiLKWr4r9jW3Z3acDu8fesN5vkUa1Vl3s&sheet=credenciales")
    fun obtenerTodoLogin(): Call<GetResponseLogin>
}

private val REQUEST_CODE_POST_NOTIFICATIONS = 1
private const val REQUEST_CODE_LOCATION_PERMISSION = 100

class MainActivity : AppCompatActivity() {
    private lateinit var etUsuario: EditText
    private lateinit var etClave: EditText
    private lateinit var btnLogin: Button
    private lateinit var gpstrackerSwitch: Switch // Declara la variable para el Switch

    private lateinit var apiService: ApiServiceLogin
    private lateinit var sharedPreferences: SharedPreferences

    private var nombreConductor: String? = null
    private var numeroMovil: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa el Switch
        gpstrackerSwitch = findViewById(R.id.gpstracker)

        // Solicitar permiso de notificación para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Restaurar el estado del switch si ya está guardado (por ejemplo, después de una rotación de pantalla o si ya está logueado)
        val isTrackingEnabled = sharedPreferences.getBoolean("isTrackingEnabled", false)
        gpstrackerSwitch.isChecked = isTrackingEnabled

        // Lógica para el cambio de estado del Switch
        gpstrackerSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Si el switch está encendido, intentar iniciar el seguimiento
                Toast.makeText(this, "Rastreo GPS activado", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("isTrackingEnabled", true).apply()
                // Asegúrate de tener nombreConductor y numeroMovil disponibles antes de iniciar el servicio
                // Si el usuario ya está logueado, estos valores ya deberían estar cargados.
                // Si no, se cargarán en el login exitoso.
                checkAndRequestLocationPermissionsAndStartService()
            } else {
                // Si el switch está apagado, detener el seguimiento
                Toast.makeText(this, "Rastreo GPS desactivado", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("isTrackingEnabled", false).apply()
                stopLocationTrackingService()
            }
        }


        // Verificar el estado de login al iniciar la actividad
        if (isLoggedIn()) {
            val savedNombreConductor = sharedPreferences.getString("nombreConductor", null)
            val savedNumeroMovil = sharedPreferences.getString("numeroMovil", null)
            nombreConductor = savedNombreConductor
            numeroMovil = savedNumeroMovil

            subscribeToFirebaseTopic()
            // Si el switch ya está encendido (estado guardado), iniciamos el servicio.
            // La llamada a checkAndRequestLocationPermissionsAndStartService() se ha movido dentro del listener del switch y el login.
            if (gpstrackerSwitch.isChecked) {
                checkAndRequestLocationPermissionsAndStartService()
            }

            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra("nombreConductor", savedNombreConductor)
                putExtra("numeroMovil", savedNumeroMovil)
            }
            startActivity(intent)
            finish()
            return
        }

        // Si no está logueado, continuar con la inicialización normal de la UI de login
        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnLogin = findViewById(R.id.btnLogin)

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl.base_url_get)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiServiceLogin::class.java)

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de notificación concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso de notificación denegado. No se podrán mostrar notificaciones.", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Permisos de ubicación concedidos.", Toast.LENGTH_SHORT).show()
                    val savedNombreConductor = sharedPreferences.getString("nombreConductor", null)
                    val savedNumeroMovil = sharedPreferences.getString("numeroMovil", null)
                    nombreConductor = savedNombreConductor
                    numeroMovil = savedNumeroMovil

                    // Si los permisos se conceden y el switch está activado, iniciar el servicio
                    if (gpstrackerSwitch.isChecked) {
                        startLocationTrackingService()
                    }
                } else {
                    Toast.makeText(this, "Permisos de ubicación denegados. No se podrá rastrear el GPS.", Toast.LENGTH_LONG).show()
                    // Si los permisos son denegados, desactivar el switch para reflejar que el rastreo no está activo
                    gpstrackerSwitch.isChecked = false
                    sharedPreferences.edit().putBoolean("isTrackingEnabled", false).apply()
                }
            }
        }
    }

    private fun performLogin() {
        val usuarioIngresado = etUsuario.text.toString().trim()
        val claveIngresada = etClave.text.toString().trim()

        if (usuarioIngresado.isEmpty() || claveIngresada.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese usuario y clave.", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.obtenerTodoLogin().enqueue(object : Callback<GetResponseLogin> {
            override fun onResponse(call: Call<GetResponseLogin>, response: Response<GetResponseLogin>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val loginList = loginResponse?.Login

                    var loginExitoso = false

                    loginList?.forEach { item ->
                        val usuarioSheet = item.usuario?.trim()
                        val cedulaSheet = item.cedula?.trim()
                        val conductorSheet = item.conductor?.trim()
                        val movilSheet = item.movil?.trim()

                        if (!usuarioSheet.isNullOrEmpty() && !cedulaSheet.isNullOrEmpty()) {
                            val ultimos4CedulaSheet = if (cedulaSheet.length >= 4) {
                                cedulaSheet.substring(cedulaSheet.length - 4)
                            } else {
                                ""
                            }

                            if (usuarioIngresado == usuarioSheet && claveIngresada == ultimos4CedulaSheet) {
                                loginExitoso = true
                                nombreConductor = conductorSheet
                                numeroMovil = movilSheet

                                sharedPreferences.edit().apply {
                                    putBoolean("isLoggedIn", true)
                                    putString("nombreConductor", nombreConductor)
                                    putString("numeroMovil", movilSheet)
                                    // También guarda el estado actual del switch al iniciar sesión
                                    putBoolean("isTrackingEnabled", gpstrackerSwitch.isChecked)
                                    apply()
                                }

                                Toast.makeText(this@MainActivity, "Login exitoso! Bienvenido: ${nombreConductor ?: "Desconocido"}", Toast.LENGTH_SHORT).show()

                                subscribeToFirebaseTopic()
                                // Si el switch está encendido al momento del login, iniciar el servicio
                                if (gpstrackerSwitch.isChecked) {
                                    checkAndRequestLocationPermissionsAndStartService()
                                }

                                val intent = Intent(this@MainActivity, MenuActivity::class.java).apply {
                                    putExtra("nombreConductor", nombreConductor)
                                    putExtra("numeroMovil", movilSheet)
                                }
                                startActivity(intent)
                                finish()
                                return@forEach
                            }
                        }
                    }

                    if (!loginExitoso) {
                        Toast.makeText(this@MainActivity, "Usuario o clave incorrectos.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener datos: ${response.code()}", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetResponseLogin>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Error de conexión", t)
            }
        })
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun subscribeToFirebaseTopic() {
        Firebase.messaging.subscribeToTopic("todos_los_usuarios")
            .addOnCompleteListener { task ->
                var msg = "Suscrito al tópico 'todos_los_usuarios'"
                if (!task.isSuccessful) {
                    msg = "Fallo al suscribirse al tópico 'todos_los_usuarios'"
                }
                Log.d("FCM_TOPIC", msg)
            }
    }

    fun logout() {
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", false)
            remove("nombreConductor")
            remove("numeroMovil")
            // Al cerrar sesión, también desactiva el seguimiento y guarda el estado
            putBoolean("isTrackingEnabled", false)
            apply()
        }
        Firebase.messaging.unsubscribeFromTopic("todos_los_usuarios")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOPIC", "Desuscrito del tópico 'todos_los_usuarios'")
                } else {
                    Log.w("FCM_TOPIC", "Fallo al desuscribirse del tópico", task.exception)
                }
            }

        stopLocationTrackingService() // Detener el servicio al cerrar sesión
        gpstrackerSwitch.isChecked = false // Desactivar el switch visualmente al cerrar sesión

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    // --- Funciones para iniciar y detener el servicio ---

    private fun checkAndRequestLocationPermissionsAndStartService() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()
        if (!fineLocationGranted) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!coarseLocationGranted) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_LOCATION_PERMISSION)
        } else {
            // Permisos ya concedidos, iniciar el servicio si el switch está activado
            if (gpstrackerSwitch.isChecked) {
                startLocationTrackingService()
            }
        }
    }

    private fun startLocationTrackingService() {
        // Asegúrate de que los permisos estén concedidos ANTES de intentar iniciar el servicio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val serviceIntent = Intent(this, LocationTrackingService::class.java).apply {
                // Asegúrate de que nombreConductor y numeroMovil tengan valores
                putExtra("nombreConductor", nombreConductor)
                putExtra("numeroMovil", numeroMovil)
            }

            // Para iniciar Foreground Service, se usa startForegroundService en Android O+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Log.d("MainActivity", "Servicio de seguimiento de ubicación iniciado.")
        } else {
            Toast.makeText(this, "Permisos de ubicación insuficientes para iniciar el seguimiento.", Toast.LENGTH_LONG).show()
            gpstrackerSwitch.isChecked = false // Desactiva el switch si no hay permisos
            sharedPreferences.edit().putBoolean("isTrackingEnabled", false).apply()
        }
    }

    private fun stopLocationTrackingService() {
        val serviceIntent = Intent(this, LocationTrackingService::class.java)
        stopService(serviceIntent)
        Log.d("MainActivity", "Servicio de seguimiento de ubicación detenido.")
    }

    override fun onDestroy() {
        super.onDestroy()
        // No es necesario detener el servicio aquí, ya que el switch lo controlará
        // o se detendrá en logout.
    }
}
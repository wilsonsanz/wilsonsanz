package com.example.segura_control

import com.example.segura_control.script_get.GetResponse
import com.example.segura_control.script_get.GetResponseLogin
import com.example.segura_control.script_get.GetResponseRutas
import com.example.segura_control.script_post.PostResponse
import com.example.segura_control.script_post.PostResponseCombustible
import com.example.segura_control.script_post.PostResponseIngreso
import com.example.segura_control.script_post.PostResponseRutas
import com.example.segura_control.script_post.PostResponseSalida
import com.example.segura_control.sheets.CombustibleData
import com.example.segura_control.sheets.IngresoData
import com.example.segura_control.sheets.PersonalData
import com.example.segura_control.sheets.RutasData
import com.example.segura_control.sheets.SalidaData
import com.example.segura_control.sheets.WikiData
import com.example.segura_control.sheets.datosData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WebService {

    // ¡ACTUALIZA ESTA LÍNEA con el ID y nombre de hoja de tu Google Sheet!
    @GET("exec?spreadsheetId=13UyqITFu8DGiLKWr4r9jW3Z3acDu8fesN5vkUa1Vl3s&sheet=personal")
    suspend fun obtenerTodoPersonal()
            : Response<GetResponse>

    @GET("exec?spreadsheetId=13UyqITFu8DGiLKWr4r9jW3Z3acDu8fesN5vkUa1Vl3s&sheet=rutas")
    suspend fun obtenerTodoRutas()
            : Response<GetResponseRutas>

    @GET("exec?spreadsheetId=13UyqITFu8DGiLKWr4r9jW3Z3acDu8fesN5vkUa1Vl3s&sheet=credenciales")
    suspend fun obtenerTodoLogin()
            : Response<GetResponseLogin>


    @POST("exec")
    suspend fun agregarPersonal(
        @Body personal: PersonalData
    ): Response<PostResponse>

    @POST("exec")
    suspend fun agregarIngreso(
        @Body ingreso: IngresoData
    ): Response<PostResponseIngreso>

    @POST("exec")
    suspend fun agregarSalida(
        @Body salida: SalidaData
    ): Response<PostResponseSalida>

    @POST("exec")
    suspend fun agregarCombustible(
        @Body combustible: CombustibleData
    ): Response<PostResponseCombustible>

    @POST("exec")
    suspend fun agregarRutas(
        @Body rutas: RutasData
    ): Response<PostResponseRutas>

    @POST("exec")
    suspend fun agregarWiki(
        @Body Wiki: WikiData
    ): Response<PostResponse>

    @POST("exec")
    suspend fun agregarDatos(
        @Body datos: datosData
    ): Response<PostResponse>




}


package com.example.myapplication.Interface;

import com.example.myapplication.data.model.LoggedInUser;
import com.example.myapplication.data.model.clsCentros;
import com.example.myapplication.data.model.clsTipoCaptura;
import com.example.myapplication.data.model.clsCompFamiliar;
import com.example.myapplication.data.model.clsEstadoEntrega;
import com.example.myapplication.data.model.clsTareoActividad;
import com.example.myapplication.data.model.clsTrabajador;
import com.example.myapplication.data.model.clsTipoDocumento;
import com.example.myapplication.data.model.clsDonaciones;
import com.example.myapplication.data.model.clsActividad;
import com.example.myapplication.data.model.clsMontCronograma;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    //declarar las rutas del api
    @GET("montaje/proyectos/ver/")
    Call<List<clsTipoCaptura>> getProyectos();

    @GET("produccion/solid/ver/{proyecto_id}")
    Call<List<clsCompFamiliar>> getSolids(
            @Path("proyecto_id") Integer proyecto_id
    );

    @GET("montaje/estados/ver/")
    Call<List<clsTipoDocumento>> getEstadosProyecto();

    @GET("montaje/incidencias/ver/")
    Call<List<clsCentros>> getIncidenciasProyecto();

    @GET("produccion/proyectos/ver/")
    Call<List<clsTipoCaptura>> getProyectosProduccion();

    @GET("produccion/tareas/ver/")
    Call<List<clsEstadoEntrega>> getTareasProyecto();

    @GET("produccion/trabajadores/ver/")
    Call<List<clsTrabajador>> getTrabajadores();

    @POST("produccion/tareo/guardar/")
    @FormUrlEncoded
    Call<clsTrabajador> guardarTareo(
            @Field("proyecto_id") Integer proyecto_id,
            @Field("tarea_id") Integer tarea_id,
            @Field("hora_inicio") String hora_inicio,
            @Field("hora_fin") String hora_fin,
            @Field("horas_trabajadas") String horas_trabajadas,
            @Field("empleados[]") Integer[] empleados,
            @Field("usuario_id") Integer usuario_id,
            @Field("solid_id") Integer solid_id
    );

    @Multipart
    @POST("montaje/supervision/guardar/")
    Call<clsDonaciones> guardarSupervision(
            @Part("proyecto_id") Integer proyecto_id,
            @Part("geolocalizacion_estado_id") Integer geolocalizacion_estado_id,
            @Part("descripcion") String descripcion,
            @Part("montaje_cronograma_id") Integer montaje_cronograma_id,
            @Part("latitud") String latitud,
            @Part("logitud") String logitud,
            @Part("usuario_id") Integer usuario_id,
            @Part("incidencias[]") Integer[] incidencia_id,
            @Part List<MultipartBody.Part> file

            );

    @POST("login/supervisor/")
    @FormUrlEncoded
    Call<LoggedInUser> Login(
            @Field("usuario") String usuario,
            @Field("contrasena") String contrasena
    );
    @POST("montaje/supervision/actividades/")
    @FormUrlEncoded
    Call<List<clsActividad>> supervision_actividades(
            @Field("id") Integer usuario_id
    );
    @GET("montaje/hitos/ver/{proyecto_id}")
    Call<List<clsMontCronograma>> getCronograma(
            @Path("proyecto_id") Integer proyecto_id
    );
    @GET("produccion/tareo/listado/")
    Call<List<clsTareoActividad>> getTareoActividades();

    @POST("produccion/tareo/editar/")
    @FormUrlEncoded
    Call<List<clsActividad>> tareo_editar(
            @Field("id") Integer id,
            @Field("hora_fin") String hora_fin,
            @Field("horas_trabajadas") String horas_trabajadas,
            @Field("usuario_id") Integer usuario_id
    );

    //declarar las rutas del api

    @GET("registros/tipocaptura/ver/{ubigeo_id}")
    Call<List<clsTipoCaptura>> getTipoCaptura(
            @Path("ubigeo_id") Integer ubigeo_id
    );

    @GET("registros/tipodocumento/ver/")
    Call<List<clsTipoDocumento>> getTipoDocumento();
/*
    @GET("montaje/estados/ver/")
    Call<List<clsCentros>> getCentros();

    @GET("montaje/estados/ver/")
    Call<List<clsCompFamiliar>> getComposicionFamiliar();*/

    @GET("registros/estadoentrega/ver/")
    Call<List<clsEstadoEntrega>> getEstadoEntrega();

    @Multipart
    @POST("montaje/supervision/guardar/")
    Call<clsDonaciones> guardarDonacion(
            @Part("tipo_captura_id") Integer tipo_captura_id,
            @Part("tipo_documento_id") Integer tipo_documento_id,
            @Part("numero_documento") Integer numero_documento,
            @Part("apellido_paterno") String apellido_paterno,
            @Part("apellido_materno") String apellido_materno,
            @Part("nombres") String nombres,
            @Part("direccion") String direccion,
            @Part("centro") String centro,
            @Part("composicion") Integer composicion_familiar_id,
            @Part("observacion") String observacion,
            @Part("estado_entrega_id") Integer estado_entrega_id,
            @Part("latitud") String latitud,
            @Part("logitud") String logitud,
            @Part("usuario_id") Integer usuario_id,
            @Part List<MultipartBody.Part> file

    );


}

package com.example.myapplication.data.model;

public class clsDonaciones {

    private int id;
    private int tipo_captura_id;
    private int tipo_documento_id;
    private int numero_documento;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipo_captura_id() {
        return tipo_captura_id;
    }

    public void setTipo_captura_id(int tipo_captura_id) {
        this.tipo_captura_id = tipo_captura_id;
    }

    public int getTipo_documento_id() {
        return tipo_documento_id;
    }

    public void setTipo_documento_id(int tipo_documento_id) {
        this.tipo_documento_id = tipo_documento_id;
    }

    public int getNumero_documento() {
        return numero_documento;
    }

    public void setNumero_documento(int numero_documento) {
        this.numero_documento = numero_documento;
    }

    public String getApellido_paterno() {
        return apellido_paterno;
    }

    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getApellido_materno() {
        return apellido_materno;
    }

    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCentro_id() {
        return centro_id;
    }

    public void setCentro_id(int centro_id) {
        this.centro_id = centro_id;
    }

    public int getComposicion_familiar_id() {
        return composicion_familiar_id;
    }

    public void setComposicion_familiar_id(int composicion_familiar_id) {
        this.composicion_familiar_id = composicion_familiar_id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getEstado_entrega_id() {
        return estado_entrega_id;
    }

    public void setEstado_entrega_id(int estado_entrega_id) {
        this.estado_entrega_id = estado_entrega_id;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLogitud() {
        return logitud;
    }

    public void setLogitud(String logitud) {
        this.logitud = logitud;
    }

    private String apellido_paterno;
    private String apellido_materno;
    private String nombres;
    private String direccion;
    private int centro_id;
    private int composicion_familiar_id;
    private String observacion;
    private int estado_entrega_id;
    private String latitud;
    private String logitud;
}

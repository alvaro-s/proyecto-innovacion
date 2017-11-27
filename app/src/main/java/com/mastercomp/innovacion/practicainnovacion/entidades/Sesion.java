package com.mastercomp.innovacion.practicainnovacion.entidades;

import java.io.Serializable;

/**
 * Created by Omar on 18/11/2017.
 */

public class Sesion implements Serializable {

    private String idSesion;
    private String idUsuario;
    private String horaInicio;
    private String horaFin;
    private String Fecha;
    private String tiempo_estudio;
    private int interrupciones;
    //GPS
    private double longitud;
    private double latitud;
    private String Ubicacion;
    //

    public Sesion(String idSesion, String idUsuario, String horaInicio, String horaFin, String fecha, String tiempo_estudio, int interrupciones,double longitud,double latitud,String Ubicacion) {
        this.idSesion = idSesion;
        this.idUsuario = idUsuario;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        Fecha = fecha;
        this.tiempo_estudio = tiempo_estudio;
        this.interrupciones = interrupciones;
        this.longitud=longitud;
        this.latitud=latitud;
        this.Ubicacion=Ubicacion;
    }

    public Sesion() {
    }

    public String getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getTiempo_estudio() {
        return tiempo_estudio;
    }

    public void setTiempo_estudio(String tiempo_estudio) {
        this.tiempo_estudio = tiempo_estudio;
    }

    public int getInterrupciones() {
        return interrupciones;
    }

    public void setInterrupciones(int interrupciones) {
        this.interrupciones = interrupciones;
    }

    public double getLongitud() {return longitud;}

    public void setLongitud(double longitud){this.longitud=longitud;}

    public double getLatitud() {return latitud;}

    public void setLatitud(double latitud){this.latitud=latitud;}

    public String getUbicacion() {return Ubicacion;}

    public void setUbicacion(String ubicacion){this.Ubicacion=Ubicacion;}
}

package com.mastercomp.innovacion.practicainnovacion.entidades;

import java.io.Serializable;

/**
 * Created by Omar on 18/11/2017.
 */

public class Usuario implements Serializable {
    private String idUsuario;
    private String nombre;
    private String apellido;

    public Usuario(String idUsuario, String nombre, String apellido) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}

package com.mastercomp.innovacion.practicainnovacion.utilidades;

/**
 * Created by Omar on 18/11/2017.
 */

public class Constantes {

    //CONSTANTES BASE DE DATOS
    public static final String NOMBRE_BASE="bd_usuarios";
    public static final int VERSION=1;
    //Constantes tabla usuario
    public static final String TABLA_USUARIO="usuario";
    public static final String CAMPO_ID="id_usuario";
    public static final String CAMPO_NOMBRE="nombre";
    public static final String CAMPO_APELLIDO="apellido";
    public static final String[] COLUMNAS_USUARIO= new String[]{CAMPO_ID, CAMPO_NOMBRE, CAMPO_APELLIDO};

    public static final String CREAR_TABLA_USUARIO="CREATE TABLE " +
            ""+TABLA_USUARIO+" ("+CAMPO_ID+" " +
            "TEXT, "+CAMPO_NOMBRE+" TEXT,"+CAMPO_APELLIDO+" TEXT)";
///sadasd
    //Constantes campos tabla sesion
    public static final String TABLA_SESION="sesion";
    public static final String CAMPO_ID_SESION="id_sesion";
    public static final String CAMPO_FECHA="fecha";
    public static final String CAMPO_HORA_INICIO="hora_inicio";
    public static final String CAMPO_HORA_FIN="hora_fin";
    public static final String CAMPO_TIEMPO_ESTUDIO="tiempo_estudio";
    public static final String CAMPO_INTERRUPCIONES="interrupciones";
    public static final String CAMPO_ID_USUARIO="id_usuario";
    public static final String CAMPO_LONGITUD="longitud";
    public static final String CAMPO_LATITUD="latitud";
    public static final String CAMPO_UBICACION="ubicacion";

    public static final String CREAR_TABLA_SESION="CREATE TABLE " +
            ""+TABLA_SESION+" ("+CAMPO_ID_SESION+" TEXT PRIMARY KEY , "
            +CAMPO_FECHA+" TEXT, "+CAMPO_HORA_INICIO+" TEXT, "+CAMPO_HORA_FIN+" TEXT,"+
            CAMPO_TIEMPO_ESTUDIO+" TEXT,"+CAMPO_INTERRUPCIONES+" INTEGER," + CAMPO_ID_USUARIO+" TEXT,"+CAMPO_LONGITUD+" TEXT,"+CAMPO_LATITUD+" TEXT,"+
            CAMPO_UBICACION+" TEXT)";
}

package com.mastercomp.innovacion.practicainnovacion.sqlite;

/**
 * Created by Omar on 18/11/2017.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.mastercomp.innovacion.practicainnovacion.utilidades.Constantes;
//Clase que maneja la coneción a la base de datos
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se crean las tablas de la base
        db.execSQL(Constantes.CREAR_TABLA_USUARIO);
        db.execSQL(Constantes.CREAR_TABLA_SESION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
        db.execSQL("DROP TABLE IF EXISTS "+Constantes.TABLA_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS "+Constantes.TABLA_SESION);
        onCreate(db);
    }

}
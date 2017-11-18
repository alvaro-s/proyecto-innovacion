package com.mastercomp.innovacion.practicainnovacion;

import android.animation.FloatEvaluator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mastercomp.innovacion.practicainnovacion.entidades.Sesion;
import com.mastercomp.innovacion.practicainnovacion.entidades.Usuario;
import com.mastercomp.innovacion.practicainnovacion.sqlite.AdminSQLiteOpenHelper;
import com.mastercomp.innovacion.practicainnovacion.utilidades.Constantes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{


    ConstraintLayout txt;                                       //el layout de la aplicación
    private TextView timer;                                     //el cronómetro

    private static Handler customHandler = new Handler();       //el manejador de los eventos del timer

    private String horainicio;
    private String horafin;

    private Button startButton;                                 //botón de comienzo
    private Button pauseButton;                                 //botón de pausa
    private Button statButton;                                  //botón de estadísticas
    private TextView interruptions;                             //contador de interrupciones
    private TextView txtSaludo;

    private Usuario usuario;
    private Sesion sesion;

    int interruptCounter = 0;

    boolean started = false;                                    //determina si el cronómetro está en funcionamiento

    private static long startTime = 0L;                         //variables que se usan para el funcionamiento del timer
    long timeInMS = 0L;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;

    //Esta clase gestiona los eventos de pantalla
    public class ScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Si se recibe un evento de pantalla apagada, se actualiza el tiempo de comienzo del timer y se espera al próximo evento
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && started) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimer, 0);

            //Si se recibe un evento de pantalla entendida, se suma el tiempo que ha pasado y se actualiza el texto del cronómetro
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && started) {
                interruptCounter++;
                interruptions.setText("" + interruptCounter);
                timeSwapBuff += timeInMS;
                customHandler.removeCallbacks(updateTimer);

            }
        }
    }

    ScreenReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GUARDAR ARCHIVO
        final Context context=this;//crear una variable context para guaradr los datos
        SharedPreferences sharprefs=getSharedPreferences("ArchivoSP",context.MODE_PRIVATE);

        //IntentFilters para recibir los eventos de pantalla
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        //Comprobar si ya existen datos previos
        usuario = consultarUsuario();
        Intent intentRegistro= new Intent(MainActivity.this, RegistroActivity.class);
        if(usuario.getNombre().equals("")){
            startActivity(intentRegistro);
            finish();
        }

        setContentView(R.layout.activity_main);

        txtSaludo=(TextView) findViewById(R.id.txtInicio);
        txtSaludo.setText(txtSaludo.getText().toString().concat(usuario.getNombre()).concat(":"));

        //Obtenemos los objetos de la interfaz por medio de su id
        timer = (TextView) findViewById(R.id.timerValue);

        interruptions = (TextView) findViewById(R.id.interruptCounter);

        statButton = (Button) findViewById(R.id.statbutton);

        //Al pulsar el botón de comienzo, ponemos en marcha la aplicación y mandamos una notificación avisando de que empezará cuando se apague la pantalla
        statButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, StatActivity.class);
                startActivity(myIntent);
            }
        });

        startButton = (Button) findViewById(R.id.startButton);

        //Al pulsar el botón de comienzo, ponemos en marcha la aplicación y mandamos una notificación avisando de que empezará cuando se apague la pantalla
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!started) {
                    started = true;
                    horainicio = getTimeString();
                    Toast.makeText(MainActivity.this, "La próxima vez que se apague la pantalla se pondrá en marcha el cronómetro", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Al pulsar el botón de fin, se detiene la aplicación y se vuelven a inicializar los valores del timer
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //SE GUARDA LA INFORMACION DE LAS INTERRUPCINES Y EL TIEMPO
                if(started) {
                    started = false;
                    /*horafin = getTimeString();
                    SharedPreferences sharpref=getPreferences(context.MODE_PRIVATE);
                    horafin = getTimeString();
                    SharedPreferences sharpref=getSharedPreferences("ArchivoSP", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharpref.edit();
                    editor.putString("MiInte",interruptions.getText().toString());
                    editor.putString("MiTiem",timer.getText().toString());
                    editor.putString("HIni", horainicio.toString());
                    editor.putString("HFin", horafin.toString());
                    editor.commit();
                    //-----------------------------//

                    timer.setText("0:00");
                    startTime = 0L;
                    timeInMS = 0L;
                    updatedTime = 0L;
                    timeSwapBuff = 0L;
                    interruptCounter = 0;
                    interruptions.setText("0");
                    //SE MUESTRA LA INFORMACION EN UNN TOAST
                    String inte=sharpref.getString("MiInte","No hay Dato");//interrupciones
                    String tiempo=sharpref.getString("MiTiem","No hay Dato");//tiempo
                    String hini = sharpref.getString("HIni", "No hay Dato"); //hora inicio
                    String hfin = sharpref.getString("HFin", "No hay Dato"); //hora inicio
                    Toast.makeText(getApplicationContext(),"Numero de Interrupciones: "+ inte +
                            "\nTiempo Total: " + tiempo + "\nHora inicio: " + hini + "\nHora fin: " +
                            hfin, Toast.LENGTH_LONG).show();*/

                    Date date= new Date();
                    sesion= new Sesion();
                    sesion.setIdSesion(UUID.randomUUID().toString());
                    sesion.setIdUsuario(usuario.getIdUsuario());
                    sesion.setInterrupciones(Integer.parseInt(interruptions.getText().toString()));
                    sesion.setFecha(date.toString());
                    sesion.setTiempo_estudio(timer.getText().toString());
                    sesion.setHoraInicio(horainicio);
                    sesion.setHoraFin(getTimeString());

                    crearSesion(sesion);

                    Toast.makeText(getApplicationContext(),"Numero de Interrupciones: "+
                            sesion.getInterrupciones() + "\nTiempo Total: " +
                            sesion.getTiempo_estudio()+ "\nHora inicio: " + sesion.getHoraInicio() +
                            "\nHora fin: " + sesion.getHoraFin(), Toast.LENGTH_LONG).show();

                 }  //--------------------//
            }
        });
        txt = (ConstraintLayout)findViewById(R.id.ctlid);
    }

    Runnable updateTimer = new Runnable() {
         //Este runnable controla el timer
        @Override
        public void run() {
            //Cuando se llama a run, se calcula el tiempo que ha pasado y el que es ahora, y se actualiza el timer
            timeInMS = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMS;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            timer.setText("" + mins + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }
    };

    private String getTimeString(){
        Calendar rightnow = Calendar.getInstance();
        String horah = "";
        if(rightnow.get(Calendar.HOUR_OF_DAY) < 10){
            horah = "0" + rightnow.get(Calendar.HOUR_OF_DAY);
        }
        else{
            horah = "" + rightnow.get(Calendar.HOUR_OF_DAY);
        }
        String horam = "";
        if(rightnow.get(Calendar.MINUTE) < 10){
            horam = "0" + rightnow.get(Calendar.MINUTE);
        }
        else{
            horam = "" + rightnow.get(Calendar.MINUTE);
        }
        String horas = "";
        if(rightnow.get(Calendar.SECOND) < 10){
            horas = "0" + rightnow.get(Calendar.SECOND);
        }
        else{
            horas = "" + rightnow.get(Calendar.SECOND);
        }
        return horah + ":" + horam + ":" + horas;
    }

    //OPERACIONES BDD

    private Usuario consultarUsuario() {
        Usuario usuario= new Usuario();
        AdminSQLiteOpenHelper conn = new AdminSQLiteOpenHelper(this,
                "bd_usuarios", null, 1);

        SQLiteDatabase bd = conn.getReadableDatabase();

        Cursor fila = bd.rawQuery("select * from usuario", null);

        if (fila.moveToFirst()) {
            usuario.setIdUsuario(fila.getString(0));
            usuario.setNombre(fila.getString(1));
            usuario.setApellido(fila.getString(2));
        }
        else
            usuario.setNombre("");
        bd.close();
        fila.close();
        return usuario;
    }

    private void crearSesion(Sesion sesion) {
        AdminSQLiteOpenHelper conn=new AdminSQLiteOpenHelper(this,"bd_usuarios",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Constantes.CAMPO_ID_SESION, sesion.getIdSesion());
        values.put(Constantes.CAMPO_FECHA, sesion.getFecha());
        values.put(Constantes.CAMPO_HORA_FIN, sesion.getHoraFin());
        values.put(Constantes.CAMPO_HORA_INICIO, sesion.getHoraInicio());
        values.put(Constantes.CAMPO_INTERRUPCIONES, sesion.getInterrupciones());
        values.put(Constantes.CAMPO_ID_USUARIO, sesion.getIdUsuario());
        values.put(Constantes.CAMPO_TIEMPO_ESTUDIO, sesion.getTiempo_estudio());

        db.insert(Constantes.TABLA_SESION,Constantes.CAMPO_ID_SESION, values);
        db.close();
    }

    private List<Sesion> consultarSesiones() {
        List<Sesion> sesionList= new ArrayList<>();

        AdminSQLiteOpenHelper conn = new AdminSQLiteOpenHelper(this,
                "bd_usuarios", null, 1);

        SQLiteDatabase bd = conn.getReadableDatabase();

        Cursor cursor = bd.rawQuery("select * from sesion", null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Sesion sesion= cursorToEntity(cursor);
                sesionList.add(sesion);
                cursor.moveToNext();
            }
            cursor.close();
        }

        bd.close();
        cursor.close();
        return sesionList;
    }

    protected Sesion cursorToEntity(Cursor cursor) {
        Sesion sesion = new Sesion();
        int idIndex;
        int fechaIndex;
        int horaInicioIndex;
        int horaFinIndex;
        int tiempoEstudioIndex;
        int interrupcionesIndex;
        int idUsuarioIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(Constantes.CAMPO_ID_SESION) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_ID_SESION);
                sesion.setIdSesion(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_FECHA) != -1) {
                fechaIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_FECHA);
                sesion.setFecha(cursor.getString(fechaIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_HORA_INICIO) != -1) {
                horaInicioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_HORA_INICIO);
                sesion.setHoraInicio(cursor.getString(horaInicioIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_HORA_FIN) != -1) {
                horaFinIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_HORA_FIN);
                sesion.setHoraFin(cursor.getString(horaFinIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_TIEMPO_ESTUDIO) != -1) {
                tiempoEstudioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_TIEMPO_ESTUDIO);
                sesion.setTiempo_estudio(cursor.getString(tiempoEstudioIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_INTERRUPCIONES) != -1) {
                interrupcionesIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_INTERRUPCIONES);
                sesion.setInterrupciones(cursor.getInt(interrupcionesIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_ID_USUARIO) != -1) {
                idUsuarioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_ID_USUARIO);
                sesion.setIdUsuario(cursor.getString(idUsuarioIndex));
            }

        }
        return sesion;
    }

}

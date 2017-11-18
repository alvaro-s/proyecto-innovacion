package com.mastercomp.innovacion.practicainnovacion;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{


    ConstraintLayout txt;                                       //el layout de la aplicación

    private static Handler customHandler = new Handler();       //el manejador de los eventos del timer

    private String horainicio;
    private String horafin;

    private Button startButton;                                 //botón de comienzo
    private Button pauseButton;                                 //botón de pausa
    private Button statButton;                                  //botón de estadísticas
    private TextView interruptions;                             //contador de interrupciones
    private Chronometer mChronometerDistraction; // este es el cronometro que contara el tiempo de distraccion
    private TextView txtSaludo;
    //String saludo;
    int interruptCounter = 0;

    private long lastPause; //esta variable se usa para buscar asignarle el valor de donde pauso el cronometro y de ahi continuar a contar el cronometro
    private boolean TimeRunning= false; // usa este booleano para saber si el tiempo esta corriendo
    private boolean ResumeTimer = false;//usa este booleano para saber si se seguira en el tiempo que se quedo cuando se apago la pantalla

    private boolean clickedStart = false;

    ScreenReceiver mReceiver;

    //Esta clase gestiona los eventos de pantalla
    public class ScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Si se recibe un evento de pantalla apagada, se actualiza el tiempo de comienzo del timer y se espera al próximo evento
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //Si el booleano ResumeTimer es true (el cronometro empezara a contar desde el valor de la ultima pausa ya que se apago la pantalla )y timerunning es true (el usuario le dio start) resume el tiempo en que se quedo el cronometro
                if (clickedStart) {
                    if(lastPause == 0){
                        mChronometerDistraction.setBase(SystemClock.elapsedRealtime());
                    }
                    else {
                        long intervalOnPause = (SystemClock.elapsedRealtime() - lastPause);
                        mChronometerDistraction.setBase(mChronometerDistraction.getBase() + intervalOnPause );
                        Log.d("Base Chrono", "" + (mChronometerDistraction.getBase()));
                        Log.d("Time now", "" + SystemClock.elapsedRealtime());
                        Log.d("Last pause", "" + lastPause);
                    }
                    mChronometerDistraction.start();
                }
                //pantalla esta apagada
            } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                //Si la pantalla se apago y el tiempo esta corriendo, deten el tiempo, coge el tiempo en que se pauso, booleano ResumeTimer es true
                if (clickedStart) {
                    interruptCounter++;
                    interruptions.setText("" + interruptCounter);
                    lastPause = SystemClock.elapsedRealtime();
                    mChronometerDistraction.stop();

                }
            }
        }
    }


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
        String nombre=sharprefs.getString("nombre", "");//interrupciones
        Intent intentRegistro= new Intent(MainActivity.this, RegistroActivity.class);
        if(nombre.equals("")){
            startActivity(intentRegistro);
            finish();
        }

        setContentView(R.layout.activity_main);

        txtSaludo=(TextView) findViewById(R.id.txtInicio);
        txtSaludo.setText(txtSaludo.getText().toString().concat(nombre).concat(":"));
        txtSaludo.setTextColor(Color.parseColor("#9C9C9C"));

        //Obtenemos los objetos de la interfaz por medio de su id

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

        mChronometerDistraction = (Chronometer) findViewById(R.id.crono);

        //Al pulsar el botón de comienzo, ponemos en marcha la aplicación y mandamos una notificación avisando de que empezará cuando se apague la pantalla
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!clickedStart) {

                   // mChronometerDistraction.stop();
                  //  mChronometerDistraction.setBase(SystemClock.elapsedRealtime());
                   // mChronometerDistraction.start(); // Comienza el cronometro de distraccion que cuenta el tiempo q la pantalla esta prendida
                    clickedStart = true;
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
                if(clickedStart) {
                    mChronometerDistraction.stop();
                    lastPause = 0;
                    clickedStart = false;
                    horafin = getTimeString();
                    SharedPreferences sharpref=getSharedPreferences("ArchivoSP", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharpref.edit();
                    editor.putString("MiInte",interruptions.getText().toString());
                    editor.putString("MiTiem",mChronometerDistraction.getText().toString());
                    editor.putString("HIni", horainicio.toString());
                    editor.putString("HFin", horafin.toString());
                    editor.commit();
                    //-----------------------------//
                    mChronometerDistraction.setBase(SystemClock.elapsedRealtime());
                    interruptCounter = 0;
                    interruptions.setText("0");

                    //SE MUESTRA LA INFORMACION EN UNN TOAST
                    String inte=sharpref.getString("MiInte","No hay Dato");//interrupciones
                    String tiempo=sharpref.getString("MiTiem","No hay Dato");//tiempo
                    String hini = sharpref.getString("HIni", "No hay Dato"); //hora inicio
                    String hfin = sharpref.getString("HFin", "No hay Dato"); //hora inicio
                    Toast.makeText(getApplicationContext(),"Numero de Interrupciones: "+ inte +
                            "\nTiempo Total: " + tiempo + "\nHora inicio: " + hini + "\nHora fin: " +
                            hfin, Toast.LENGTH_LONG).show();
                 }  //--------------------//
            }
        });
        txt = (ConstraintLayout)findViewById(R.id.ctlid);
    }

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

}

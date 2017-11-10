package com.mastercomp.innovacion.practicainnovacion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    ConstraintLayout txt;                                       //el layout de la aplicación
    private TextView timer;                                     //el cronómetro

    private static Handler customHandler = new Handler();       //el manejador de los eventos del timer

    private Button startButton;                                 //botón de comienzo
    private Button pauseButton;                                 //botón de pausa
    private TextView interruptions;                             //contador de interrupciones
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
        //IntentFilters para recibir los eventos de pantalla
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_main);

        //Obtenemos los objetos de la interfaz por medio de su id
        timer = (TextView) findViewById(R.id.timerValue);

        interruptions = (TextView) findViewById(R.id.interruptCounter);

        startButton = (Button) findViewById(R.id.startButton);

        //Al pulsar el botón de comienzo, ponemos en marcha la aplicación y mandamos una notificación avisando de que empezará cuando se apague la pantalla
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!started) {
                    started = true;
                    Toast.makeText(MainActivity.this, "La próxima vez que se apague la pantalla se pondrá en marcha el cronómetro", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Al pulsar el botón de fin, se detiene la aplicación y se vuelven a inicializar los valores del timer
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(started) {
                    started = false;
                    timer.setText("0:00");
                    startTime = 0L;
                    timeInMS = 0L;
                    updatedTime = 0L;
                    timeSwapBuff = 0L;
                    interruptCounter = 0;
                    interruptions.setText("0");
                }
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

}

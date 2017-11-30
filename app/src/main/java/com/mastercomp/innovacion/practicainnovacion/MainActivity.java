package com.mastercomp.innovacion.practicainnovacion;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.mastercomp.innovacion.practicainnovacion.entidades.Sesion;
import com.mastercomp.innovacion.practicainnovacion.entidades.Usuario;
import com.mastercomp.innovacion.practicainnovacion.sqlite.AdminSQLiteOpenHelper;
import com.mastercomp.innovacion.practicainnovacion.utilidades.Constantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    private Usuario usuario;
    private Sesion sesion;

    int interruptCounter = 0;

    private long lastPause; //esta variable se usa para buscar asignarle el valor de donde pauso el cronometro y de ahi continuar a contar el cronometro
    private boolean TimeRunning= false; // usa este booleano para saber si el tiempo esta corriendo
    private boolean ResumeTimer = false;//usa este booleano para saber si se seguira en el tiempo que se quedo cuando se apago la pantalla

    private boolean clickedStart = false;

    ScreenReceiver mReceiver;
    //GPS
    private TextView mensaje1;
    private TextView mensaje2;
    private TextView tvUbicacion;

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
                if (clickedStart) {
                ConstraintLayout view = (ConstraintLayout) findViewById(R.id.ctlid);

                String tiempoestudio = mChronometerDistraction.getText().toString();
                String horaahora = getTimeString();

                String [] tiempoSplit = tiempoestudio.split(":");

                float fTiempoAprovechado = 0;
                for(int i =0 ; i < tiempoSplit.length; i++){
                    fTiempoAprovechado += Integer.parseInt(tiempoSplit[i])*Math.pow(60, tiempoSplit.length - 1 - i);
                }


                String [] hiniSplit = horainicio.split(":");
                String [] hfinSplit = horaahora.split(":");

                float fini = Integer.parseInt(hiniSplit[0])*3600 + Integer.parseInt(hiniSplit[1])*60 + Integer.parseInt(hiniSplit[2]);
                float ffin = Integer.parseInt(hfinSplit[0])*3600 + Integer.parseInt(hfinSplit[1])*60 + Integer.parseInt(hfinSplit[2]);

                float fTiempoTotal = ffin - fini;

                int blue = (int) (161 + (fTiempoAprovechado/fTiempoTotal) * 67);

                int red = 389 - blue;
                view.setBackgroundColor(Color.argb(255, red, 169, blue));
                //Si la pantalla se apago y el tiempo esta corriendo, deten el tiempo, coge el tiempo en que se pauso, booleano ResumeTimer es true

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
        clickedStart = false;
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
        //GPS
        mensaje1 = (TextView) findViewById(R.id.tvlongitud);
        mensaje2 = (TextView) findViewById(R.id.tvlatitud);
        tvUbicacion=(TextView)findViewById(R.id.tvUbicacion);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
        // ---
        txtSaludo=(TextView) findViewById(R.id.txtInicio);
        txtSaludo.setText(txtSaludo.getText().toString().concat(usuario.getNombre()).concat(":"));

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
                    mChronometerDistraction.setBase(SystemClock.elapsedRealtime());
                    // Comienza el cronometro de distraccion que cuenta el tiempo q la pantalla esta prendida
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
                    Calendar cal = Calendar.getInstance();
                    sesion= new Sesion();
                    sesion.setIdSesion(UUID.randomUUID().toString());
                    sesion.setIdUsuario(usuario.getIdUsuario());
                    sesion.setInterrupciones(Integer.parseInt(interruptions.getText().toString()));
                    sesion.setFecha(cal.get(Calendar.YEAR) + "-" + String.format("%02d", cal.get(Calendar.MONTH ) + 1) + "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH ) + 1)) ;
                    sesion.setTiempo_estudio(mChronometerDistraction.getText().toString());
                    sesion.setHoraInicio(horainicio);
                    sesion.setHoraFin(getTimeString());
                    //GPS
                    sesion.setLongitud(mensaje1.getText().toString());
                    sesion.setLatitud(mensaje2.getText().toString());
                    sesion.setUbicacion(tvUbicacion.getText().toString());

                    crearSesion(sesion);

                    mChronometerDistraction.setBase(SystemClock.elapsedRealtime());
                    interruptCounter = 0;
                    interruptions.setText("0");
                    Toast.makeText(getApplicationContext(),"Numero de Interrupciones: "+
                            sesion.getInterrupciones() + "\nTiempo Total: " +
                            sesion.getTiempo_estudio()+ "\nHora inicio: " + sesion.getHoraInicio() +
                            "\nHora fin: " + sesion.getHoraFin(), Toast.LENGTH_LONG).show();
                 }  //--------------------//
            }
        });
        txt = (ConstraintLayout)findViewById(R.id.ctlid);
    }
    //GSP
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(MainActivity.this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
       mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
       //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1.setText("agregada");
        tvUbicacion.setText("");

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    tvUbicacion.setText(DirCalle.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;
        public MainActivity getMainActivity() {return mainActivity;}
        public void setMainActivity(MainActivity mainActivity) {this.mainActivity = this.mainActivity;}
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            //String Text = "Mi ubicacion actual es: " + "\n Lat = "
            //        + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1.setText(""+loc.getLongitude());
            mensaje2.setText(""+loc.getLatitude());
            setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            mensaje1.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            mensaje1.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    //--------------------
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
        values.put(Constantes.CAMPO_HORA_INICIO, sesion.getHoraInicio());
        values.put(Constantes.CAMPO_HORA_FIN, sesion.getHoraFin());
        values.put(Constantes.CAMPO_TIEMPO_ESTUDIO, sesion.getTiempo_estudio());
        values.put(Constantes.CAMPO_INTERRUPCIONES, sesion.getInterrupciones());
        values.put(Constantes.CAMPO_ID_USUARIO, sesion.getIdUsuario());

        //GPS
        values.put(Constantes.CAMPO_LONGITUD,sesion.getLongitud());
        values.put(Constantes.CAMPO_LATITUD,sesion.getLatitud());
        values.put(Constantes.CAMPO_UBICACION,sesion.getUbicacion());


        db.insert(Constantes.TABLA_SESION,Constantes.CAMPO_ID_SESION, values);
        db.close();
    }

}

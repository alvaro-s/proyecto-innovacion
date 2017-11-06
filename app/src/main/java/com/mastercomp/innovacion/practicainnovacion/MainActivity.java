package com.mastercomp.innovacion.practicainnovacion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    //GPS-------------
    TextView mensaje1;
    //TextView mensaje2;
    //GPS-------------
    ConstraintLayout txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt= (ConstraintLayout)findViewById(R.id.ctlid);//se agrego
        txt.setOnTouchListener(this);

        //GPS 1
        mensaje1 = (TextView) findViewById(R.id.textView);

       // mensaje2 = (TextView) findViewById(R.id.textView2);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
//        } else {
//           locationStart();
//        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this, "HOLA MUNDO 100", Toast.LENGTH_LONG).show();
        locationStart();
        return false;
    }
//GPS--------------------------------
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
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
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

       // mensaje1.setText("Localizacion agregada");
       // mensaje2.setText("");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
//    public void setLocation(Location loc) {
//        //Obtener la direccion de la calle a partir de la latitud y la longitud
//        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
//            try {
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                List<Address> list = geocoder.getFromLocation(
//                        loc.getLatitude(), loc.getLongitude(), 1);
//                if (!list.isEmpty()) {
//                    Address DirCalle = list.get(0);
//                    mensaje2.setText("Mi direccion es: \n"
//                            + DirCalle.getAddressLine(0));
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1.setText(Text);
            //this.mainActivity.setLocation(loc);
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
        @Override
        public void onProviderEnabled(String provider) {
           // mensaje1.setText("GPS Activado");
        }

        @Override
        public void onProviderDisabled(String provider) {
           // mensaje1.setText("GPS Desactivado");
        }
        public void setMainActivity(MainActivity mainActivity) {
            //this.mainActivity = mainActivity;
        }
    }
    //GPSSSSSS--------------------------------------------------
}

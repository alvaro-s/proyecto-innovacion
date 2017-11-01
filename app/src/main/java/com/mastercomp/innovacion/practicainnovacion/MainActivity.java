package com.mastercomp.innovacion.practicainnovacion;

import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    ConstraintLayout txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt= (ConstraintLayout)findViewById(R.id.ctlid);//se agrego
        txt.setOnTouchListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this, "HOLA MUNDO 100", Toast.LENGTH_LONG).show();
        return false;
    }
}

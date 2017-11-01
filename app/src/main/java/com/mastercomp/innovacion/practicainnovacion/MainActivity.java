package com.mastercomp.innovacion.practicainnovacion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    ConstraintLayout txt;
    private TextView timer;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMS = 0L;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = (TextView) findViewById(R.id.timerValue);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i("Check","Screen went ON");
                    timeSwapBuff += timeInMS;
                    customHandler.removeCallbacks(updateTimer);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.i("Check","Screen went OFF");
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimer, 0);
                }
            }
        }, intentFilter);

        setContentView(R.layout.activity_main);
        txt = (ConstraintLayout)findViewById(R.id.ctlid);//se agrego
        txt.setOnTouchListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this, "HOLA MUNDO 100", Toast.LENGTH_LONG).show();
        return false;
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            timeInMS = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMS;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int ms = (int) (updatedTime % 1000);
            timer.setText("" + mins + ":"
                    + String.format("%02d", secs)+ ":"
                    + String.format("%03d", ms));
            customHandler.postDelayed(this, 0);

        }
    };
}

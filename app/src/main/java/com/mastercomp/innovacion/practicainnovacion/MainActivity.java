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

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    ConstraintLayout txt;
    private TextView timer;
    private static long startTime = 0L;
    private static Handler customHandler = new Handler();

    boolean started = false;

    long timeInMS = 0L;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;

    public class ScreenReceiver extends BroadcastReceiver{
        public boolean wasScreenOn = true;
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimer, 0);
                started = true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                timeSwapBuff += timeInMS;
                customHandler.removeCallbacks(updateTimer);
                started = false;
            }
        }
    }

    ScreenReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_main);
        timer = (TextView) findViewById(R.id.timerValue);



        txt = (ConstraintLayout)findViewById(R.id.ctlid);
        txt.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this, "HOLA MUNDO 100", Toast.LENGTH_LONG).show();
        return false;
    }

     Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
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

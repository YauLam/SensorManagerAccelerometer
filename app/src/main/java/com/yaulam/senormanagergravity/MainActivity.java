package com.yaulam.senormanagergravity;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private HorizontalScrollView hsvGravity;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int x = 0;
    private boolean isAutoScroll = true;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hsvGravity = (HorizontalScrollView) findViewById(R.id.hsv_gravity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensor = null;

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            List<Sensor> gravSensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
            for(int i=0; i<gravSensors.size(); i++) {
                if ((gravSensors.get(i).getVendor().contains("Google LLC")) &&
                        (gravSensors.get(i).getVersion() == 3)){
                    // Use the version 3 gravity sensor.
                    mSensor = gravSensors.get(i);
                }
            }
        }
        if (mSensor == null){
            // Use the accelerometer.
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }else{
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
            }
        }

        hsvGravity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        isAutoScroll = false;
                        break;
                }
                return false;
            }
        });

        hsvGravity.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                x = i;
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float xGravityValue = sensorEvent.values[0];
        x += 10 * xGravityValue;
        if (Math.abs(xGravityValue)>2){
            if (isAutoScroll){
                hsvGravity.smoothScrollTo(x, 0);
            }
        }else{
            isAutoScroll = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

package com.alienonwork.gforcemeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ForceMeterActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private TextView mAccelerationTextView;
    private TextView mMaxAccelerationTextView;
    private float mCurrentAcceleration = 0;
    private float mMaxAcceleration = 0;

    private final double calibration = SensorManager.STANDARD_GRAVITY;

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            double a = Math.round(Math.sqrt(Math.pow(x, 2) +
                                            Math.pow(y, 2) +
                                            Math.pow(z, 2)));

            mCurrentAcceleration = Math.abs((float)(a-calibration));

            if (mCurrentAcceleration > mMaxAcceleration)
                mMaxAcceleration = mCurrentAcceleration;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccelerationTextView = findViewById(R.id.acceleration);
        mMaxAccelerationTextView = findViewById(R.id.maxAcceleration);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("gForceUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                String currentG = mCurrentAcceleration / SensorManager.STANDARD_GRAVITY + "Gs";
                mAccelerationTextView.setText(currentG);
                mAccelerationTextView.invalidate();
                String maxG = mMaxAcceleration / SensorManager.STANDARD_GRAVITY + "Gs";
                mMaxAccelerationTextView.setText(maxG);
                mMaxAccelerationTextView.invalidate();
            }
        });
    }
}

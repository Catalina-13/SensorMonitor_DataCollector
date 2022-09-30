package com.example.sensormonitor_datacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accel;
    private boolean sensing = false;
    private SensorEventListener sensorEventListener;
    private HandlerThread workerThread;
    private Handler handlerWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        workerThread = new HandlerThread("Worker Thread");
        workerThread.start();
        handlerWorker = new Handler(workerThread.getLooper());

        ((Button)findViewById(R.id.buttonStartStop)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sensing = !sensing;
                if (sensing){
                    startSensing();
                    ((Button)findViewById(R.id.buttonStartStop)).setText("Stop");
                }else{
                    stopSensing();
                    ((Button)findViewById(R.id.buttonStartStop)).setText("Start");
                }

            }
        });
    }
    protected void startSensing(){
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long ts = event.timestamp;
                    float[] values = event.values.clone();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.valueAccelX)).setText(String.format("%.3f", values[0]));
                            ((TextView) findViewById(R.id.valueAccelY)).setText(String.format("%.3f", values[0]));
                            ((TextView) findViewById(R.id.valueAccelZ)).setText(String.format("%.3f", values[0]));
                            Log.d("", Thread.currentThread().getName());
                        }
                    });
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                return;
            }
        };
        sensorManager.registerListener(sensorEventListener, accel, 10000, handlerWorker);
    }

    protected  void stopSensing(){
        sensorManager.unregisterListener(sensorEventListener, accel);
    }
}
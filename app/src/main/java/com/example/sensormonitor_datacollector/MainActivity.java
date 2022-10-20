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
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gravity;
    private Sensor gyroscope;
    private CustomSensorEventListener sensorEventListener;
    private HandlerThread workerThread;
    private Handler handlerWorker;
    private RadioGroup activitiesRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activitiesRadioGroup = findViewById(R.id.activities);
        sensorEventListener = new CustomSensorEventListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gravity = sensorManager.getDefaultSensor((Sensor.TYPE_GRAVITY));
        gyroscope = sensorManager.getDefaultSensor((Sensor.TYPE_GYROSCOPE));
        workerThread = new HandlerThread("Worker Thread");
        workerThread.start();
        handlerWorker = new Handler(workerThread.getLooper());

        ((Button)findViewById(R.id.buttonStartStop)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (sensorEventListener.isSensing()){
                    stopSensing();
                    ((Button)findViewById(R.id.buttonStartStop)).setText("Start");
                }else{
                    startSensing();
                    ((Button)findViewById(R.id.buttonStartStop)).setText("Stop");
                }

            }
        });

        ((Button)findViewById(R.id.buttonPauseResume)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (sensorEventListener.isSensing()){
                    pauseSensing();
                    ((Button)findViewById(R.id.buttonPauseResume)).setText("Resume");
                }else{
                    resumeSensing();
                    ((Button)findViewById(R.id.buttonPauseResume)).setText("Pause");
                }

            }
        });

        ((Button)findViewById(R.id.buttonDiscard)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                discard();
                ((Button) findViewById(R.id.buttonDiscard)).setText("Discard");
            }
        });
    }
    protected void startSensing(){
        sensorManager.registerListener(sensorEventListener, accel, 10000, handlerWorker);
        sensorManager.registerListener(sensorEventListener, gravity, 10000, handlerWorker);
        sensorManager.registerListener(sensorEventListener, gyroscope, 10000, handlerWorker);
        sensorEventListener.setSensing(true);
        //handlerWorker.postDelayed((Runnable) this, interval); // delayed start
    }
    protected void stopSensing(){
        sensorManager.unregisterListener(sensorEventListener, accel);
        sensorManager.unregisterListener(sensorEventListener, gravity);
        sensorManager.unregisterListener(sensorEventListener, gyroscope);
        sensorEventListener.setSensing(false);
        //handlerWorker.postDelayed((Runnable) this, interval); // early stop
    }
    protected void pauseSensing() {
        sensorEventListener.setSensing(false);
    }
    protected void resumeSensing(){
        sensorEventListener.setSensing(true);
    }
    protected void discard(){
        sensorEventListener.discard();
    }

    protected int getActivityClass() {
        int id = activitiesRadioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.activity_other:
                return 0;
            case R.id.activity_walking:
                return 1;
            case R.id.activity_running:
                return 2;
            case R.id.activity_standing:
                return 3;
            case R.id.activity_sitting:
                return 4;
            case R.id.activity_upstairs:
                return 5;
            case R.id.activity_downstairs:
                return 6;
            default:
                return -1;

        }
    }
}
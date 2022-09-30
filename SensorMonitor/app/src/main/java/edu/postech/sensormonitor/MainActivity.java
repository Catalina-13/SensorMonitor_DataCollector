package edu.postech.sensormonitor;

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

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mSensing = false;
    private SensorEventListener mSensorEventListener;
    private HandlerThread mWorkerThread;
    private Handler mHandlerWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccel         = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mWorkerThread = new HandlerThread("Worker Thread");
        mWorkerThread.start();
        mHandlerWorker = new Handler(mWorkerThread.getLooper());

        ((Button)findViewById(R.id.buttonStartStop)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mSensing = !mSensing;
                if (mSensing){
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
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long ts = event.timestamp;
                    float[] values = event.values.clone();
                    Log.d("", Thread.currentThread().getName());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.valueAccelX)).setText(String.format("%.3f", values[0]));
                            ((TextView)findViewById(R.id.valueAccelY)).setText(String.format("%.3f", values[1]));
                            ((TextView)findViewById(R.id.valueAccelZ)).setText(String.format("%.3f", values[2]));
                        }
                    });
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                return;
            }
        };
        mSensorManager.registerListener(mSensorEventListener, mAccel, 10000, mHandlerWorker);

    }

    protected void stopSensing(){
        mSensorManager.unregisterListener(mSensorEventListener, mAccel);
    }

}
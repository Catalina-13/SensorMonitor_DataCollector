package com.example.sensormonitor_datacollector;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class CustomSensorEventListener implements SensorEventListener {
    private final MainActivity mainActivity;

    private final long sensingTimeDelayMicroseconds = 5*1000*1000;
    private final long minimumSensingTimeMicroseconds = 10*60*1000*1000;
    private long startSensingTimestampMicroSec;

    public boolean isSensing() {
        return isSensing;
    }

    @SuppressLint("DefaultLocale")
    private void storeSensingData(long endSensingTimestampMicroSec) {

        String[] accelerometerData = new String[7];
        String[] gravityData = new String[7];
        String[] gyroscopeData = new String[7];

        for (int i =0; i < 7; i++) {
            accelerometerData[i] = "";
            gravityData[i] = "";
            gyroscopeData[i] = "";
        }

        for (Sample sample : samples) {
            //Log.e("Timestamp", (sample.timestamp - endSensingTimestampMicroSec + sensingTimeDelayMicroseconds) + "");
            //Log.e("Timestamp", (sample.timestamp - startSensingTimestampMicroSec - sensingTimeDelayMicroseconds) + "");
            if (sample.timestamp - startSensingTimestampMicroSec - sensingTimeDelayMicroseconds >= 0 &&
                    sample.timestamp - endSensingTimestampMicroSec + sensingTimeDelayMicroseconds < 0 &&
                    endSensingTimestampMicroSec - startSensingTimestampMicroSec - sensingTimeDelayMicroseconds*2 >= minimumSensingTimeMicroseconds) {
                if (sample.sensorEventType == Sensor.TYPE_LINEAR_ACCELERATION) {
                    accelerometerData[sample.activity_class] += (String.format("%d,%d,%.9e,%.9e,%.9e\n",
                            sample.activity_class,
                            sample.timestamp,
                            sample.value_x,
                            sample.value_y,
                            sample.value_z));
                } else if (sample.sensorEventType == Sensor.TYPE_GRAVITY) {
                    gravityData[sample.activity_class] += (String.format("%d,%d,%.9e,%.9e,%.9e\n",
                            sample.activity_class,
                            sample.timestamp,
                            sample.value_x,
                            sample.value_y,
                            sample.value_z));
                } else if (sample.sensorEventType == Sensor.TYPE_GYROSCOPE) {
                    gyroscopeData[sample.activity_class] += (String.format("%d,%d,%.9e,%.9e,%.9e\n",
                            sample.activity_class,
                            sample.timestamp,
                            sample.value_x,
                            sample.value_y,
                            sample.value_z));
                }
            }
        }
        FileReadWrite readWrite = new FileReadWrite(this.mainActivity);
        for (int i = 0; i < 7; i++) {
            String acc = accelerometerData[i];
            if (acc.length() > 0) {
                readWrite.writeToFile(i, "linear.csv", acc);
            }
        }
        for (int i = 0; i < 7; i++) {
            String grav = gravityData[i];
            if (grav.length() > 0) {
                readWrite.writeToFile(i, "gravity.csv", grav);
            }
        }
        for (int i = 0; i < 7; i++) {
            String gyro = gyroscopeData[i];
            if (gyro.length() > 0) {
                readWrite.writeToFile(i, "gyro.csv", gyro);
            }
        }
        discard();
        //Log.e("Data", readWrite.readFromFile(0, "gyro.csv"));
    }

    public void setSensing(boolean sensing) {
        isSensing = sensing;
        if (sensing){
            startSensingTimestampMicroSec = SystemClock.elapsedRealtimeNanos() / 1000;
        }
        else {
            storeSensingData(SystemClock.elapsedRealtimeNanos() / 1000);
        }
    }

    private boolean isSensing = false;
    private LinkedList<Sample> samples = new LinkedList<>();
    //private File fileName;

    public CustomSensorEventListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isSensing) return;
        int eventType = event.sensor.getType();
        if (eventType == Sensor.TYPE_LINEAR_ACCELERATION ||
                eventType == Sensor.TYPE_GRAVITY ||
                eventType == Sensor.TYPE_GYROSCOPE) {
            long ts = TimeUnit.NANOSECONDS.toMicros(event.timestamp); // time span in microseconds
            int activity_class = mainActivity.getActivityClass();
            float[] values = event.values.clone();

            Sample sample = new Sample(ts, activity_class, eventType, values[0], values[1], values[2]);

            samples.add(sample);
            //Log.e("Sample TS", String.valueOf(sample.timestamp));
            //Log.e("Sample AC", String.valueOf(sample.activity_class));
            //Log.e("Sample x", String.valueOf(sample.value_x));
            //Log.e("Sample y", String.valueOf(sample.value_y));
            //Log.e("Sample z", String.valueOf(sample.value_z));
        }
    }

    public void discard() {
        samples = new LinkedList<>();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private static class Sample{
        long timestamp;
        int activity_class;
        int sensorEventType;
        float value_x, value_y, value_z;

        public Sample(long timestamp, int activity_class, int sensorEventType, float value_x, float value_y, float value_z) {
            this.timestamp = timestamp;
            this.activity_class = activity_class;
            this.sensorEventType = sensorEventType;
            this.value_x = value_x;
            this.value_y = value_y;
            this.value_z = value_z;
        }
    }
}

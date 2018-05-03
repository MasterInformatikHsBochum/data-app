package com.example.lisa.challenge;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class SensorThreads extends AsyncTask {
    List<SensorTyp> sensorTypList;

    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    /** Looper thread that listen to SensorEvent */
    private Looper sensorLooper;

    /** Is the inner looper thread started. */
    private boolean isRunning;

    /** List of registered listeners  */
    private final ArrayList<SensorEventListener> registeredListeners =
            new ArrayList<SensorEventListener>();

    public SensorThreads(SensorManager sensorManager, SensorEventListener sensorEventListener,List<SensorTyp>  sensorTypList)
    {
        this.sensorManager=sensorManager;
        this.sensorEventListener=sensorEventListener;
        this.sensorTypList=sensorTypList;

    }


    @Override
    protected Void doInBackground(Object... objects) {
        if(isRunning)
        {return null;}
        HandlerThread sensorThread = new HandlerThread("sensor") {
            @Override
            protected void onLooperPrepared() {
                Handler handler = new Handler(Looper.myLooper());

                for (SensorTyp sensorTyp : sensorTypList) {
                    registerListener(sensorTyp, 10000);
                }

            }

        };
        sensorThread.start();
        sensorLooper = sensorThread.getLooper();  // Blocks till looper is ready.
        isRunning = true;
        return null;

    }



    /**
     * Stops the looper and deregister the listener from the sensor manager.
     */
    public void stopSenor() {
        if (!isRunning) {
            // No need to stop.
            return;
        }

        for (SensorTyp sensorTyp : sensorTypList) {
            deaktivateListener(sensorTyp);
        }
        sensorEventListener = null;

        sensorLooper.quit();
        sensorLooper = null;
        isRunning = false;
    }

    /**
     * Aktiviert das Messen der Sensordaten
     * @param sensorTyp
     * @param time Zeitintervall in Microsekunden, in denen die Werte abgefragt werden
     */
    private void registerListener(SensorTyp sensorTyp, int time) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), time);
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), time);
        } else if (sensorTyp == SensorTyp.LIGHT) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), time);
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }

    private void deaktivateListener(SensorTyp sensorTyp) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        } else if (sensorTyp == SensorTyp.LIGHT) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }
}

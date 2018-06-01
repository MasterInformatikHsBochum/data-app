package com.example.lisa.challenge;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Rotate extends MyNavigation {

    ChallangeAction challange;

    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    Button start;
    Button cancel;
    TextView result;

    //Gibt an, ob die Aufnahme gestartet worden ist.
    private Boolean startMeasure;
    //Gibt an, ob das Handy sich in der Hosentasche befindet und die Umgebung dunkel ist
    private Float aktuellerLichwert = 100f;
    private int aktuelleGradZahl;

    //Ergebnis
    private int ergebnisGradzahl;
    private int anzahlDrehungen;

    public AudioManager am;
    private int maxV;
    private int curV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(Rotate.this, R.layout.rotate);
        super.onCreate(savedInstanceState);
        challange = new ChallangeAction();
        setUpIds();
        setUpListenerButtons();
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        setUpSensorListener();
        setUpListenerButtons();
        this.am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxV = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curV = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void beep() {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxV ,am.FLAG_PLAY_SOUND);

    }

    public void berechneWinkel(SensorEvent event)
    {//Berechne Winkel
        float[] orientation = new float[3];
        float[] rMat = new float[9];
        // calculate th rotation matrix
        SensorManager.getRotationMatrixFromVector(rMat, event.values);
        // get the azimuth value (orientation[0]) in degree
        aktuelleGradZahl =(int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[2]) + 360) % 360;
    }

    private void setUpSensorListener() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    //Bewegungssensor Liniar
                    case Sensor.TYPE_ROTATION_VECTOR:
                        berechneWinkel(event);
                        break;
                    //Lichsensor
                    case Sensor.TYPE_LIGHT:
                        aktuellerLichwert = event.values[0];
                        break;
                }

            }
        };
    }

    private void setUpListenerButtons() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                setStartMeasure(true);
                aktuellerLichwert=100f;
                actionRotate();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messungBeenden();
            }
        });
    }

    private void setUpIds() {
        start = findViewById(R.id.buttonStartRotateJump);
        cancel = findViewById(R.id.buttonCancelRotateJump);
        result = findViewById(R.id.textResultRotate);
    }

    private void actionRotate() {

        registerListener(SensorTyp.LIGHT);
        registerListener(SensorTyp.ROTATE);
        RotationBerechnenThread thread = new RotationBerechnenThread(this, challange);
        thread.execute();
        Log.d("runnable","Messung ist angeschaltet: " + startMeasure);
    }

    public void ueberTrageMessergebnis() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // result.setText("Du bist " + getErgebnisGradzahl() + "mm hoch gesprungen");
                result.setText("Du hast dich um " + ergebnisGradzahl + "Grad gedreht. Das sind " + anzahlDrehungen + " Drehungen");
                messungBeenden();
            }
        });
    }

    private void messungBeenden()
    {
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        startMeasure=false;
        deaktivateListener(SensorTyp.LIGHT);
        deaktivateListener(SensorTyp.ROTATE);
    }

    /**
     * Aktiviert das Messen der Sensordaten
     *
     * Info:
     * challange.getAbtastrate() = 100 -> registerListener erwartet ein Integerwert mit einer Abstastrate pro Us,
     * also 100 * 1000 * 1000.
     * @param sensorTyp
     */
    public void registerListener(SensorTyp sensorTyp) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.LIGHT) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.ROTATE) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), challange.getAbtastrate());
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }

    public void deaktivateListener(SensorTyp sensorTyp) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        } else if (sensorTyp == SensorTyp.LIGHT) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        } else if (sensorTyp == SensorTyp.ROTATE) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }


    public void setAktuellerLichwert(Float aktuellerLichwert) {
        this.aktuellerLichwert = aktuellerLichwert;
    }

    public int getAnzahlDrehungen() {
        return anzahlDrehungen;
    }

    public void setAnzahlDrehungen(int anzahlDrehungen) {
        this.anzahlDrehungen = anzahlDrehungen;
    }

    public int getAktuelleGradZahl() {
        return Math.abs(aktuelleGradZahl);
    }

    public void setAktuelleGradZahl(int aktuelleGradZahl) {
        this.aktuelleGradZahl = aktuelleGradZahl;
    }

    public Boolean getStartMeasure() {
        return startMeasure;
    }

    public Float getAktuellerLichwert() {
        return aktuellerLichwert;
    }

    public void setStartMeasure(Boolean startMeasure) {
        this.startMeasure = startMeasure;
    }

    public int getErgebnisGradzahl() {
        return ergebnisGradzahl;
    }

    public void setErgebnisGradzahl(int gemesseneHoehe) {
        this.ergebnisGradzahl = gemesseneHoehe;
    }
}

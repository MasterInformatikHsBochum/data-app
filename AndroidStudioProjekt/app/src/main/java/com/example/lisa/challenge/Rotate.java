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
    TextView highscoreText;
    HighScore highScore;

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
        highScore = new HighScore(this);
        setUpIds();
        setUpListenerButtons();
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        setUpSensorListener();
        setUpListenerButtons();
        this.am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxV = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curV = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void beep() {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxV, am.FLAG_PLAY_SOUND);

    }

    private void setUpSensorListener() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            float[] mGeomagnetic = null;
            float[] mGravity = null;

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {

                switch (event.sensor.getType()) {
                    //Bewegungssensor Liniar
                    case Sensor.TYPE_ROTATION_VECTOR:
                        mGravity = event.values;
                        berechneWinkel(event);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mGeomagnetic = event.values;
                        //Lichsensor
                    case Sensor.TYPE_LIGHT:
                        aktuellerLichwert = event.values[0];
                        break;
                }
               // berecheMitGravitation();

            }

            private void berechneWinkel(SensorEvent event) {//Berechne Winkel
                float[] orientation = new float[3];
                float[] rMat = new float[9];

                //Schreibe in rMAth die Rotationsmatrix aus dem Event
                SensorManager.getRotationMatrixFromVector(rMat, event.values);

                //Zerlege die Rotationsmatrik in die Einzelnden bestandteile
                SensorManager.getOrientation(rMat, orientation);
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                float pitch = orientation[1]; //Neigung
                float roll = orientation[2]; //Rollbewegung um y

                // get the azimuth value (orientation[0]) in degree
                //val[0] in degrees [-180 , +180]
                aktuelleGradZahl = (int) (Math.toDegrees(roll) + 360) % 360;

               // aktuelleGradZahl =(int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[2]) + 360) % 360;
            }

            private void berecheMitGravitation(){
                if (mGravity != null && mGeomagnetic != null) {

                    // * <p>
                    //     * Computes the inclination matrix <b>I</b> as well as the rotation matrix
                    //     * <b>R</b> transforming a vector from the device coordinate system to the
                    //     * world's coordinate system which is defined as a direct orthonormal basis,
                    //     * where:
                    //     * </p>
                    float R[] = new float[9];
                    float I[] = new float[9];

                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    //if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    float pitch = orientation[1]; //Neigung
                    float roll = orientation[2]; //Rollbewegung um y
                    aktuelleGradZahl = (int) (Math.toDegrees(roll) +360) % 360;
                    // }
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
                aktuellerLichwert = 100f;
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
        highscoreText = findViewById(R.id.highscoreRotate);
        //Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
        float[] r_score = highScore.get_R_score();
        highscoreText.setText(challange.createHiscoreString((int)r_score[0],(int)r_score[1],(int)r_score[2],"Drehungen"));

    }



    private void actionRotate() {

        registerListener(SensorTyp.LIGHT);
        registerListener(SensorTyp.ROTATE);
        registerListener(SensorTyp.MAGNETIC_FIELD);
        RotationBerechnenThread thread = new RotationBerechnenThread(this, challange);
        thread.execute();
        Log.d("runnable", "Messung ist angeschaltet: " + startMeasure);
    }

    public void ueberTrageMessergebnis() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // result.setText("Du bist " + getErgebnisGradzahl() + "mm hoch gesprungen");
                result.setText("Du hast dich um " + ergebnisGradzahl + "Grad gedreht. Das sind " + anzahlDrehungen + " Drehungen");
                highScore.new_R_score(anzahlDrehungen);
                //Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
                float[] r_score = highScore.get_R_score();
                highscoreText.setText(challange.createHiscoreString((int)r_score[0],(int)r_score[1],(int)r_score[2], "Drehungen"));
                messungBeenden();
            }
        });
    }

    private void messungBeenden() {
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        startMeasure = false;
        deaktivateListener(SensorTyp.LIGHT);
        deaktivateListener(SensorTyp.ROTATE);
        deaktivateListener(SensorTyp.MAGNETIC_FIELD);
    }

    /**
     * Aktiviert das Messen der Sensordaten
     * <p>
     * Info:
     * challange.getAbtastrate() = 100 -> registerListener erwartet ein Integerwert mit einer Abstastrate pro Us,
     * also 100 * 1000 * 1000.
     *
     * @param sensorTyp
     */
    public void registerListener(SensorTyp sensorTyp) {
        if (sensorTyp == SensorTyp.MAGNETIC_FIELD) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.ACCELEROMETER) {
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
        if (sensorTyp == SensorTyp.MAGNETIC_FIELD) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        } else if (sensorTyp == SensorTyp.ACCELEROMETER) {
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

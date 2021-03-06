package com.example.lisa.challenge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;

public class HighJump extends MyNavigation {

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
    private Float aktuelleBeschleunigung = 0f;
    private Float aktuellerLichwert = 100f;

    private Float gemesseneHoeheMillimeter;
    private Float gemesseneHoeheZentimeter;

    private Float gemesseneHoechstGeschwindigkeitMProS;
    private Float gemesseneHoechstGeschwindigkeitCmProS;

    public AudioManager am;
    private int maxV;
    private int curV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(HighJump.this,R.layout.highjump );
        super.onCreate(savedInstanceState);
        challange = new ChallangeAction();
        highScore = new HighScore(getSharedPreferences("Challange", Context.MODE_PRIVATE));
        setUpIds();
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        setUpSensorListener();
        setUpListenerButtons();
        this.am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxV = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curV = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("LAUTSTÄRKE","maximale Lautstärke: " + maxV);
        Log.d("LAUTSTÄRKE","aktuelle Lautstärke: " + curV);
    }

    public void beep() {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxV ,am.FLAG_PLAY_SOUND);

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
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        aktuelleBeschleunigung = event.values[1];
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
                actionHighJump();
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
        start = findViewById(R.id.buttonStartHighJump);
        cancel = findViewById(R.id.buttonCancelHighJump);
        result = findViewById(R.id.textResult);
        highscoreText = findViewById(R.id.highscoreHighjump);
        //Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
        float[] h_score = highScore.get_HS_score();
        highscoreText.setText(challange.createHiscoreString(h_score[0],h_score[1],h_score[2],"cm/s"));
    }

    private void actionHighJump() {

        registerListener(SensorTyp.LIGHT);
        registerListener(SensorTyp.ACCELEROMETER);
        HighJumpBerechnenThread thread = new HighJumpBerechnenThread(this, challange);
        thread.execute();
        Log.d("runnable","Messung ist angeschaltet: " + startMeasure);
    }

    public void ueberTrageMessergebnis() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // result.setText("Du bist " + getGemesseneHoeheMillimeter() + "mm hoch gesprungen");
                //result.setText("Du bist " + getGemesseneHoeheZentimeter() + "cm hoch gesprungen");
                result.setText("Deine Höchste Geschwindigkeit beträgt " + getGemesseneHoechstGeschwindigkeitCmProS() + " cm/s");
                highScore.new_HS_score(getGemesseneHoechstGeschwindigkeitCmProS());
                //Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
                float[] h_score = highScore.get_HS_score();
                highscoreText.setText(challange.createHiscoreString(h_score[0],h_score[1],h_score[2],"cm/s"));
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
        deaktivateListener(SensorTyp.ACCELEROMETER);
    }

    public Float getAktuelleBeschleunigung() {
        return aktuelleBeschleunigung;
    }

    public Float getAktuellerLichwert() {
        return aktuellerLichwert;
    }

    public Boolean getStartMeasure() {
        return startMeasure;
    }

    public void setStartMeasure(Boolean startMeasure) {
        this.startMeasure = startMeasure;
    }

    public Float getGemesseneHoeheMillimeter() {
        return gemesseneHoeheMillimeter;
    }

    public void setGemesseneHoeheMillimeter(Float gemesseneHoehe) {
        this.gemesseneHoeheMillimeter = gemesseneHoehe;
    }

    public Float getGemesseneHoeheZentimeter() {
        return gemesseneHoeheZentimeter;
    }

    public void setGemesseneHoeheZentimeter(Float gemesseneHoehe) {
        this.gemesseneHoeheZentimeter = gemesseneHoehe;
    }

    public Float getGemesseneHoechstGeschwindigkeitMProS(){ return gemesseneHoechstGeschwindigkeitMProS; }

    public void setGemesseneHoechstGeschwindigkeitMProS(Float gemesseneGeschwindigkeit){
        this.gemesseneHoechstGeschwindigkeitMProS = gemesseneGeschwindigkeit;
    }

    public Float getGemesseneHoechstGeschwindigkeitCmProS(){
        return gemesseneHoechstGeschwindigkeitCmProS;
    }

    public void setGemesseneHoechstGeschwindigkeitCmProS(Float gemesseneGeschwindigkeit){
        this.gemesseneHoechstGeschwindigkeitCmProS = gemesseneGeschwindigkeit;
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
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),  challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), challange.getAbtastrate());
        } else if (sensorTyp == SensorTyp.LIGHT) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), challange.getAbtastrate());
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
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }

}

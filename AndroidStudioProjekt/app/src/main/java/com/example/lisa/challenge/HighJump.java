package com.example.lisa.challenge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HighJump extends AppCompatActivity {

    ChallangeAction challange;

    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    Button start;
    Button cancel;
    TextView result;

    //Gibt an, ob die Aufnahme gestartet worden ist.
    private Boolean startMeasure;
    //Gibt an, ob das Handy sich in der Hosentasche befindet und die Umgebung dunkel ist
    private Float aktuelleHoehe = 0f;
    private Float aktuellerLichwert = 100f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highjump);
        challange = new ChallangeAction();
        setUpIds();
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        setUpSensorListener();
        setUpListenerButtons();
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
                    case Sensor.TYPE_ACCELEROMETER:
                        aktuelleHoehe = event.values[1];
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
                actionHighJump();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                setStartMeasure(false);
            }
        });
    }

    private void setUpIds() {
        start = findViewById(R.id.buttonStartHighJump);
        cancel = findViewById(R.id.buttonCancelHighJump);
        result = findViewById(R.id.textResult);
    }

    private void actionHighJump() {

        registerListener(SensorTyp.LIGHT, 10000);
        registerListener(SensorTyp.ACCELEROMETER, 10000);
        HighJumpBarechnenThread thread = new HighJumpBarechnenThread(this, challange);
        thread.execute();
//        Float ergebniss = 0f;
//        try {
//            ergebniss = (Float) thread.execute().get();
//            result.setText("Du bist " + ergebniss + "mm hoch gesprungen");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        // /Am Ende der Messung den Cancel Button wieder deaktivieren
//        cancel.setVisibility(View.GONE);
//        start.setVisibility(View.VISIBLE);
//        startMeasure=false;
//        deaktivateListener(SensorTyp.LIGHT);
//        deaktivateListener(SensorTyp.ACCELEROMETER);
    }

    public Float getAktuelleHoehe() {
        return aktuelleHoehe;
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

    /**
     * Aktiviert das Messen der Sensordaten
     *
     * @param sensorTyp
     * @param time      Zeitintervall in Microsekunden, in denen die Werte abgefragt werden
     */
    public void registerListener(SensorTyp sensorTyp, int time) {
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

    public void deaktivateListener(SensorTyp sensorTyp) {
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

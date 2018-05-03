package com.example.lisa.challenge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HighJump extends AppCompatActivity {

    ChallangeAction challange;

    SensorManager sensorManager;
    SensorEventListener sensorEventListener;

    Button start;
    Button cancel;
    TextView result;

    //Gibt an, ob die Aufnahme gestartet worden ist.
    Boolean startMeasure;
    //Gibt an, ob das Handy sich in der Hosentasche befindet und die Umgebung dunkel ist
    Float hoeheAusgangspunkt = 0f;
    Float aktuelleHoehe = 0f;
    Float aktuellerLichwert = 100f;

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
    SensorEvent sensorEvent;

    private void setUpSensorListener() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                sensorEvent=event;
                switch (event.sensor.getType()) {
                    //Bewegungssensor Liniar
                    case Sensor.TYPE_ACCELEROMETER:
                        Log.d("runnable", "Hoehe " + event.values[1]);
                        aktuelleHoehe = event.values[1];
                        break;
                    //Lichsensor
                    case Sensor.TYPE_LIGHT:
                        Log.d("runnable", "Lichtwert" + event.values[0]);
                        aktuellerLichwert = event.values[0];
                        result.setText("Aktueller Lichwert " + aktuellerLichwert);
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
                startMeasure = true;
                actionHighJump();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                startMeasure = false;
            }
        });
    }

    private void setUpIds() {
        start = findViewById(R.id.buttonStartHighJump);
        cancel = findViewById(R.id.buttonCancelHighJump);
        result = findViewById(R.id.textResult);


    }

    private void actionHighJump() {

         SensorThreads sensorThreads = new SensorThreads(sensorManager, sensorEventListener, Arrays.asList(SensorTyp.LIGHT, SensorTyp.ACCELEROMETER));
         sensorThreads.execute();

        //Warte so lange bis das Smartphone in der Hosentasche ist und die Messung läuft
        while (!challange.isDark(aktuellerLichwert)&& startMeasure) {
            try {
                Log.d("runnable", "Warte das Handy in die Hosentesche gesteckt wird. Aktueller Lichtewert = " + aktuellerLichwert);
                TimeUnit.SECONDS.sleep(1);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Die Messung wurde abgebrochen
        if (startMeasure == false) {
            return;
        }


        //Bestimme die Bezugshöhe von der die Person abspringt
        hoeheAusgangspunkt = aktuelleHoehe;

        challange.startCountdown(3);

        // Solange es dunkel ist, und maximal 4 Sekunden Werte messen
        List<Float> messwerte = new ArrayList<>();
        long t = System.currentTimeMillis();
        long end = t + 4000;
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        while (challange.isDark(aktuellerLichwert) && startMeasure && System.currentTimeMillis() < end) {
            messwerte.add(aktuelleHoehe);
        }

        if (startMeasure) {
            Log.d("runnable", "berechne Ergebniss");
            float ergebniss = challange.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            Log.d("runnable", "Du bist " + ergebniss + "mm hoschgesprungen");
            result.setText("Du bist " + ergebniss + "mm hoch gesprungen");
        }

        //Am Ende der Messung den Cancel Button wieder deaktivieren
        cancel.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        startMeasure = false;
         sensorThreads.stopSenor();

    }



}

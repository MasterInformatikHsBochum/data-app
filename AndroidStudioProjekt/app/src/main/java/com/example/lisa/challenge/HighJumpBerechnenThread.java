package com.example.lisa.challenge;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

public class HighJumpBerechnenThread extends AsyncTask{

    private HighJump hochsprung;
    ChallangeAction challangeAction;



    public HighJumpBerechnenThread(HighJump hochsprung, ChallangeAction challangeAction)
    {
        this.hochsprung=hochsprung;
        this.challangeAction =challangeAction;
    }

    /**
     *  Warte so lange bis das Smartphone in der Hosentasche ist und die Messung läuft
     */
    private void waitToBeDark()
    {
        while (!challangeAction.isDark(hochsprung.getAktuellerLichwert())&& hochsprung.getStartMeasure()) {

            try {
                Log.d("runnable", "Warte das Handy in die Hosentesche gesteckt wird. Aktueller Lichtewert = " + hochsprung.getAktuellerLichwert());
                TimeUnit.SECONDS.sleep(1);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("runnable", " >>>>> Aktueller Lichtewert = " + hochsprung.getAktuellerLichwert());
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        waitToBeDark();

        //Die Messung wurde abgebrochen
        if (hochsprung.getStartMeasure() == false) {
            return null;
        }

        //Wenn das Handy in die Hosentasche gesteckt wird, warte 3 Sekunden und gebe dann den Starton wieder
        final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sound.startTone(ToneGenerator.TONE_PROP_BEEP, 2000);
        Log.d("beep", "beep");
        hochsprung.beep();

        // Solange es dunkel ist, und maximal 4 Sekunden Werte messen
        List<Float> messwerte = new ArrayList<>();
        long t = System.currentTimeMillis();
        long end = t + 3000;
        long time = System.currentTimeMillis();
        long step = 1; //ms
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        double v=0;
        double dt =1/(double)challangeAction.getAbtastrate(); // Hundert Werte Pro sekunde
        Log.d("runnable", "dt: " + dt);
        double x = 0;
        while (challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure() && (System.currentTimeMillis() < end)) {


            /*
             * 1. Möglichkeit: Alle Beschleunigungwerte positiv machen und dann Gesamtstrecke durch 2 teilen
             * 2. Möglichkeit: Nur positive Beschleunigungswerte zu Berechnung verwenden.
             */
            if(System.currentTimeMillis()>= t) {
                messwerte.add(hochsprung.getAktuelleBeschleunigung());
                double a = Math.abs(hochsprung.getAktuelleBeschleunigung());
                x = a*dt*dt+v*dt+x;
                v = a*dt+v;
                t = t + step;
            }

        }

        x = x/2;
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "m");
        float ergebniss = (float) x;
        x = x * 100;
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "cm");

        if (hochsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebnis");
            //float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            //float ergebniss = challangeAction.numIntegrationStandard(messwerte);
            // next line do not work
            float ergebnis2 = 1;//float ergebnis2 = challangeAction.doppeltIntegration(messwerte);
            Log.d("runnable", "Du bist " + ergebnis2 + " m hochgesprungen");
            ergebnis2 = ergebnis2 * (float)100.0;
            Log.d("runnable", "Du bist " + ergebnis2 + " cm hochgesprungen");
            hochsprung.setStartMeasure(false);
            hochsprung.setGemesseneHoeheZentimeter(ergebniss);
            hochsprung.ueberTrageMessergebnis();
            return ergebniss;
        }

        return null;
    }


}

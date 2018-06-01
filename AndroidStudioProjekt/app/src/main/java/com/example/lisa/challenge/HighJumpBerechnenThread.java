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

    /**
     * Wenn das Handy in die Hosentasche gesteckt wird, warte 3 Sekunden und gebe dann den Starton wieder
     */
    public void generateTone() {

        final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sound.startTone(ToneGenerator.TONE_PROP_BEEP, 2000);
        Log.d("beep", "beep");
        hochsprung.beep();
    }



    @Override
    protected Object doInBackground(Object[] objects) {

        waitToBeDark();

        //Die Messung wurde abgebrochen
        if (hochsprung.getStartMeasure() == false) {
            return null;
        }

        generateTone();

        //Bestimme die Bezugshöhe von der die Person abspringt
        float hoeheAusgangspunkt = hochsprung.getAktuelleHoehe();
        Log.d("runnable", "Die Ausgangshöhe ist" + hoeheAusgangspunkt);

        // Solange es dunkel ist, und maximal 4 Sekunden Werte messen
        List<Float> messwerte = new ArrayList<>();
        long t = System.currentTimeMillis();
        long end = t + 4000;
        long time = System.currentTimeMillis();
        long step = 10; //ms
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        double v=0;
        double dt =1/(double)challangeAction.getAbtastrate(); // Hundert Werte Pro sekunde
        Log.d("runnable", "dt: " + dt);
        double x = 0;
        while (challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure() && (System.currentTimeMillis() < end)) {

            if(System.currentTimeMillis()>= t) {
                messwerte.add(hochsprung.getAktuelleHoehe());
                double a= hochsprung.getAktuelleHoehe();
                x = a*dt*dt+v*dt+x;
                v = a*dt+v;
                t = t + step;
            }

        }
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "m");
        x = x * 100;
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "cm");

        if (hochsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebnis");
            //float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            float ergebniss = challangeAction.numIntegrationStandard(messwerte);
            // next line do not work
            // float ergebniss = challangeAction.doppeltIntegration(messwerte);
            Log.d("runnable", "Du bist " + ergebniss + " m hochgesprungen");
            ergebniss = ergebniss * (float)100.0;
            Log.d("runnable", "Du bist " + ergebniss + " cm hochgesprungen");
            hochsprung.setStartMeasure(false);
            hochsprung.setGemesseneHoeheZentimeter(ergebniss);
            hochsprung.ueberTrageMessergebnis();
            return ergebniss;
        }

        return null;
    }


}

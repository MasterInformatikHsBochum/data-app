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
        float hoeheAusgangspunkt = hochsprung.getAktuelleBeschleunigung();
        Log.d("runnable", "Die Ausgangshöhe ist" + hoeheAusgangspunkt);




        List<Float> messwerte = new ArrayList<>();
        long t = System.currentTimeMillis();
        long end = t + 3000;
        long time = System.currentTimeMillis();
        long step = 1000/challangeAction.getAbtastrate(); //ms (1/f * 1000[ms/s])
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        float v=0;
        float dt =1/(float)challangeAction.getAbtastrate(); // Hundert Werte Pro sekunde
        Log.d("runnable", "dt: " + dt + " & step: " + step);
        float x = 0;
        while (challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure() && (System.currentTimeMillis() < end)) {


            /*
             * 1. Möglichkeit: Alle Beschleunigungwerte positiv machen und dann Gesamtstrecke durch 2 teilen
             * 2. Möglichkeit: Nur positive Beschleunigungswerte zu Berechnung verwenden.
             */
            if(System.currentTimeMillis()>= t) {
                messwerte.add(hochsprung.getAktuelleBeschleunigung());
                //float a = Math.abs(hochsprung.getAktuelleBeschleunigung());
                //x = a*dt*dt+v*dt+x;
                //v = a*dt+v;
                t = t + step;
            }

        }
        List<Float> messwerte_glat= daten_gleatten(messwerte,11);
    /*
        x = x/2;
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "m");
        x = x * 100;
        Log.d("runnable", "Die Sprunghöhe beträgt " + x + "cm");
    */
        if (hochsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebnis");
            //float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            //float ergebniss = challangeAction.numIntegrationStandard(messwerte);
            //x = challangeAction.doppeltIntegration(messwerte);
            x = challangeAction.doubleIntegrationLinearAccerlaration(messwerte);
            //x = challangeAction.wegMitDurschnittsberechnung(messwerte);
            Log.d("runnable", "Die Sprunghöhe beträgt " + x + "m");
            x = x * 100;
            Log.d("runnable", "Die Sprunghöhe beträgt " + x + "cm");
            hochsprung.setStartMeasure(false);
            hochsprung.setGemesseneHoeheZentimeter(x);
            hochsprung.ueberTrageMessergebnis();
            return x;
        }

        return null;
    }
    public List<Float>daten_gleatten(List<Float> messwerte, int anzahlelemente) {
        List<Float> messwerte_glat = new ArrayList<>();
        for (int i = 0; i < messwerte.size()-anzahlelemente; i += anzahlelemente) {
            float sum = 0;
            for (float messwert : messwerte.subList(i, i + (anzahlelemente-1))) {
                sum += messwert;
            }
            messwerte_glat.add(sum / messwerte.subList(i, i + (anzahlelemente-1)).size());
        }
        return messwerte_glat;
    }


}

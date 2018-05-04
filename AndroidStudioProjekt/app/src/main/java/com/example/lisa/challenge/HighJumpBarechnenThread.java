package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HighJumpBarechnenThread extends AsyncTask {

    private HighJump hochsprung;
    ChallangeAction challangeAction;

    public HighJumpBarechnenThread(HighJump hochsprung, ChallangeAction challangeAction)
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
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        waitToBeDark();

        //Die Messung wurde abgebrochen
        if (hochsprung.getStartMeasure() == false) {
            return null;
        }

        //Bestimme die Bezugshöhe von der die Person abspringt
        float hoeheAusgangspunkt = hochsprung.getAktuelleHoehe();
        Log.d("runnable", "Die Ausgangshöhe ist" + hoeheAusgangspunkt);

        //Wenn das Handy in die Hosentasche gesteckt wird, warte 3 Sekunden und gebe dann den Starton wieder
        final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sound.startTone(ToneGenerator.TONE_PROP_BEEP);


        // Solange es dunkel ist, und maximal 4 Sekunden Werte messen
        List<Float> messwerte = new ArrayList<>();
        long t = System.currentTimeMillis();
        long end = t + 4000;
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        while (challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure() && System.currentTimeMillis() < end) {
            messwerte.add(hochsprung.getAktuelleHoehe());
        }

        if (hochsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebniss");
            float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            Log.d("runnable", "Du bist " + ergebniss + "mm hoschgesprungen");
            hochsprung.setStartMeasure(false);
            hochsprung.setGemesseneHoehe(ergebniss);
            hochsprung.ueberTrageMessergebnis();
            return ergebniss;
        }

        return null;
    }


}

package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BroadJumpBerechnenThread extends AsyncTask {

    private BroadJump weitsprung;
    ChallangeAction challangeAction;



    public BroadJumpBerechnenThread(BroadJump weitsprung, ChallangeAction challangeAction)
    {
        this.weitsprung=weitsprung;
        this.challangeAction =challangeAction;
    }

    /**
     *  Warte so lange bis das Smartphone in der Hosentasche ist und die Messung läuft
     */
    private void waitToBeDark()
    {
        while (!challangeAction.isDark(weitsprung.getAktuellerLichwert())&& weitsprung.getStartMeasure()) {

            try {
                Log.d("runnable", "Warte das Handy in die Hosentesche gesteckt wird. Aktueller Lichtewert = " + weitsprung.getAktuellerLichwert());
                TimeUnit.SECONDS.sleep(1);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("runnable", " >>>>> Aktueller Lichtewert = " + weitsprung.getAktuellerLichwert());
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
        weitsprung.beep();
    }



    @Override
    protected Object doInBackground(Object[] objects) {

        waitToBeDark();

        //Die Messung wurde abgebrochen
        if (weitsprung.getStartMeasure() == false) {
            return null;
        }

        generateTone();

        //Bestimme die Bezugshöhe von der die Person abspringt
        float hoeheAusgangspunkt = weitsprung.getAktuelleBeschleunigung();
        Log.d("runnable", "Die Ausgangshöhe ist" + hoeheAusgangspunkt);


        // Solange es dunkel ist, und maximal 3 Sekunden Werte messen
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
        while (challangeAction.isDark(weitsprung.getAktuellerLichwert()) && weitsprung.getStartMeasure() && (System.currentTimeMillis() < end)) {


            /*
             * 1. Möglichkeit: Alle Beschleunigungwerte positiv machen und dann Gesamtstrecke durch 2 teilen
             * 2. Möglichkeit: Nur positive Beschleunigungswerte zu Berechnung verwenden.
             */
            if(System.currentTimeMillis()>= t) {
                messwerte.add(weitsprung.getAktuelleBeschleunigung());
                //float a = Math.abs(weitsprung.getAktuelleBeschleunigung());
                //x = a*dt*dt+v*dt+x;
                //v = a*dt+v;
                t = t + step;
            }

        }
        int i = 0;
        for(float zahl: messwerte) {
            //Log.d("runnable", /*"RohDaten " + (++i) + ": " + */""+zahl);
        }

        List<Float> messwerte_glat = moving_average(messwerte, 9);
        i = 0;
        Log.d("runnable", "geglättet --------------------------------------> ");
        for(float zahl: messwerte_glat) {
            //Log.d("runnable", /*"geglättet " + (++i) + ": " +*/ ""+zahl);
        }

        if (weitsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebnis");
            //float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            //float ergebniss = challangeAction.numIntegrationStandard(messwerte);
            //x = challangeAction.doppeltIntegration(messwerte);
            //x = challangeAction.doubleIntegrationLinearAccerlaration(messwerte);
            //x = challangeAction.wegMitDurschnittsberechnung(messwerte);
            x = (float) challangeAction.simpsonrule_final(messwerte, dt, "modusBroadJump");
            Log.d("runnable", "Die Geschwindigkeit beträgt " + x + "m/s");
            x = x * 100;

            weitsprung.setStartMeasure(false);
            weitsprung.setGemesseneHoechstGeschwindigkeitCmProS(x);
            weitsprung.ueberTrageMessergebnis();
            return x;
        }

        return null;
    }

    public List<Float> moving_average(List<Float> messwerte, int anzahlelemente) {
        List<Float> messwerte_glat = new ArrayList<>();
        List<Float> messwerte_subslist = messwerte.subList(0,anzahlelemente-1);
        for (int i = anzahlelemente; i < messwerte.size()-1 ; i++) {
            messwerte_glat.add(berechneduchschnitt(messwerte_subslist));
            messwerte_subslist.remove(0);
            messwerte_subslist.add(messwerte.get(i));
        }
        return messwerte_glat;
    }

    public float berechneduchschnitt(List<Float> messwerte) {
        float sum = 0;
        for (int i = 0; i < messwerte.size(); i++) {
            sum += messwerte.get(i);
        }
        return sum/messwerte.size();

    }

}

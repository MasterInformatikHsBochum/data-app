package com.example.lisa.challenge;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.content.Context;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import android.widget.Toast;

public class HighJumpBerechnenThread extends AsyncTask {

    private HighJump hochsprung;
    ChallangeAction challangeAction;


    public HighJumpBerechnenThread(HighJump hochsprung, ChallangeAction challangeAction) {
        this.hochsprung = hochsprung;
        this.challangeAction = challangeAction;
    }

    /**
     * Warte so lange bis das Smartphone in der Hosentasche ist und die Messung läuft
     */
    private void waitToBeDark() {
        while (!challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure()) {

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
        long step = 1000 / challangeAction.getAbtastrate(); //ms (1/f * 1000[ms/s])
        Log.d("runnalbe", "starte Aufzeichnung der Werte");
        float v = 0;
        float dt = 1 / (float) challangeAction.getAbtastrate(); // Hundert Werte Pro sekunde
        Log.d("runnable", "dt: " + dt + " & step: " + step);
        float x = 0;
        while (challangeAction.isDark(hochsprung.getAktuellerLichwert()) && hochsprung.getStartMeasure() && (System.currentTimeMillis() < end)) {


            /*
             * 1. Möglichkeit: Alle Beschleunigungwerte positiv machen und dann Gesamtstrecke durch 2 teilen
             * 2. Möglichkeit: Nur positive Beschleunigungswerte zu Berechnung verwenden.
             */
            if (System.currentTimeMillis() >= t) {
                messwerte.add(hochsprung.getAktuelleBeschleunigung());
                //float a = Math.abs(hochsprung.getAktuelleBeschleunigung());
                //x = a*dt*dt+v*dt+x;
                //v = a*dt+v;
                t = t + step;
            }

        }


        /*
         * Messwerte in Textdatei abspeichern
         * Zugriff wird verweigert
         */
        //speichereMesswerte(messwerte);

        int i = 0;
        for(float zahl: messwerte) {
            Log.d("runnable", /*"RohDaten " + (++i) + ": " + */""+zahl);
        }

        List<Float> messwerte_glat = moving_average(messwerte, 11);
        i = 0;
        Log.d("runnable", "geglättet --------------------------------------> ");
        for(float zahl: messwerte_glat) {
            Log.d("runnable", /*"geglättet " + (++i) + ": " +*/ ""+zahl);
        }




        if (hochsprung.getStartMeasure()) {
            Log.d("runnable", "berechne Ergebnis");
            //float ergebniss = challangeAction.getDiffenenzOfStartValueAndMax(hoeheAusgangspunkt, messwerte);
            //float ergebniss = challangeAction.numIntegrationStandard(messwerte);
            //x = challangeAction.doppeltIntegration(messwerte);
            //---> x = challangeAction.doubleIntegrationLinearAccerlaration(messwerte);
            //x = challangeAction.wegMitDurschnittsberechnung(messwerte);
            x = (float) challangeAction.simpsonrule_final(messwerte, dt);
            Log.d("runnable", "Die Geschwindigkeit beträgt " + x + "m/s");
            x = x * 100;

            hochsprung.setStartMeasure(false);
            hochsprung.setGemesseneHoechstGeschwindigkeitCmProS(x);
            hochsprung.ueberTrageMessergebnis();
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

    /*
     * Messwerte in Textdatei abspeichern
     */
    public void speichereMesswerte(List<Float> messwerte){

        String strMesswerte = "";
        for(float zahl: messwerte){
            strMesswerte = strMesswerte + zahl + "\n";
        }

        try {
            File myFile = new File("/sdcard/messwerte.txt");
            myFile.mkdirs();
            myFile.createNewFile();
            Log.d("runnable", "Datei wurde erstellt");
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(strMesswerte);

            myOutWriter.close();
            fOut.close();
            Log.d("runnable", "wurde drangehangen");
        }catch(IOException ioe){
            Log.d("runnable", "hat nicht geklappt");
            Log.d("runnable", ioe.getMessage());
        }



    }




}

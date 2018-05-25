package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;


public class ChallangeAction {

       final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    /**
     * Die Abtastrate, die zum Aufzeichnen und Berechnen von Distanzen verwendet wird.
     */
    private static final int abtastrate=100;


    /**
     * Wenn der Lichtwert unter 10 ist (es ist Dunkel) wird true zurückgegeben, sonst false
     */
    public boolean isDark(float aktuellerLichtwert)
    {
            return aktuellerLichtwert<10? true: false;
    }

    public float getDiffenenzOfStartValueAndMax(float startValue, List<Float> messwerte)
    {
        if(messwerte==null || messwerte.isEmpty())
        {
            return 0;
        }
        //Maximum ermitteln
        Float max = Collections.max(messwerte);
        Log.d("runnable","Maximaler Messwert "+ max + "- startwert"+ startValue);

        if(max<startValue)
        {
            return 0;
        }
        return max- startValue;
    }

    public float numIntegrationStandard(List<Float> messwerte){

        /**
         Float[] floatArray = messwerte.toArray(new Float[messwerte.size()]);
         float periodendauer = 1/abtastrate;

         for(int i = 0; i < floatArray.length; i++){
         floatArray[i] = floatArray[i] * periodendauer * periodendauer;
         }

         float flaeche = 0;

         for (int i = 0; i < floatArray.length; i++){
         flaeche = flaeche + floatArray[i];
         }
         **/
        float periodendauer = (float)1.0/(float)abtastrate;
        Log.d("runnable", "Periodendauer " + periodendauer);
        List<Float> listFlaeche = new ArrayList<>();
        for(int i = 0; i < messwerte.size(); i++){
            listFlaeche.add( messwerte.get(i) * periodendauer * periodendauer);
        }
//        Log.d("runnable", "listFlaeche(0) " + listFlaeche.get(0));

        float flaeche = 0;

        for(int i = 0; i < listFlaeche.size(); i++){
            if(listFlaeche.get(i)>=0){
                flaeche = flaeche + listFlaeche.get(i);
            }
            //Log.d("runnable","zurückgelegter weg: "+ flaeche);
        }


        return flaeche;
    }

    public int getAbtastrate() {
        return abtastrate;
    }

}
